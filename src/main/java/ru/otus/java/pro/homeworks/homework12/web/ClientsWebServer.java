package ru.otus.java.pro.homeworks.homework12.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.pro.homeworks.homework12.service.DbService;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ClientsWebServer implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(ClientsWebServer.class);
    private static final String COOKIE_NAME = "SESSION_ID";
    private static final int MAX_FORM_BYTES = 64 * 1024;
    private final HttpServer server;
    private final ExecutorService executor;
    private final DbService dbService;
    private final SessionManager sessions = new SessionManager();
    private final String adminLogin;
    private final String adminPassword;

    public ClientsWebServer(InetSocketAddress address, DbService dbService, String adminLogin, String adminPassword) throws IOException {
        this.dbService = Objects.requireNonNull(dbService);
        this.adminLogin = Objects.requireNonNull(adminLogin);
        this.adminPassword = Objects.requireNonNull(adminPassword);

        if (adminLogin.isBlank() || adminPassword.isBlank()) {
            throw new IllegalArgumentException("Логин и пароль администратора не должны быть пустыми");
        }

        server = HttpServer.create(address, 0);
        executor = Executors.newVirtualThreadPerTaskExecutor();
        server.setExecutor(executor);
        server.createContext("/", this::handle);
    }

    public void start() {
        server.start();
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    private void handle(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
            exchange.getResponseHeaders().set("Content-Security-Policy", "default-src 'none'; style-src 'unsafe-inline'; form-action 'self'; " + "base-uri 'none'; frame-ancestors 'none'");

            switch (exchange.getRequestURI().getRawPath()) {
                case "/" -> home(exchange);
                case "/login" -> login(exchange);
                case "/clients" -> clients(exchange);
                case "/logout" -> logout(exchange);
                default -> error(exchange, 404, "Страница не найдена");
            }
        } catch (HttpError e) {
            error(exchange, e.status, e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Request failed: {} {}", exchange.getRequestMethod(), exchange.getRequestURI().getRawPath(), e);
            error(exchange, 500, "Не удалось выполнить запрос. Попробуйте позже.");
        } finally {
            exchange.close();
        }
    }

    private void home(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "GET");
        if (currentSession(exchange) != null) {
            redirect(exchange, "/clients");
        } else {
            send(exchange, 200, HtmlPages.login(null));
        }
    }

    private void login(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "POST");
        Map<String, String> form = readForm(exchange);
        if (!secureEquals(adminLogin, form.get("username")) || !secureEquals(adminPassword, form.get("password"))) {
            send(exchange, 401, HtmlPages.login("Неверный логин или пароль"));

            return;
        }

        sessions.remove(sessionId(exchange));
        SessionManager.Session session = sessions.create();
        exchange.getResponseHeaders().add("Set-Cookie", COOKIE_NAME + "=" + session.id() + "; Path=/; HttpOnly; SameSite=Lax; Max-Age=" + SessionManager.LIFETIME.toSeconds());
        redirect(exchange, "/clients");
    }

    private void clients(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "GET", "POST");
        SessionManager.Session session = currentSession(exchange);
        if (session == null) {
            redirect(exchange, "/");

            return;
        }

        if ("GET".equals(exchange.getRequestMethod())) {
            boolean created = "created=1".equals(exchange.getRequestURI().getRawQuery());
            send(exchange, 200, HtmlPages.clients(dbService.getAllClients(), session.csrfToken(), null, "", "", "", created));

            return;
        }

        Map<String, String> form = readForm(exchange);
        requireCsrf(session, form);
        String name = form.getOrDefault("name", "");
        String street = form.getOrDefault("street", "");
        String phones = form.getOrDefault("phones", "");

        try {
            dbService.createClient(name, street, phones);
        } catch (IllegalArgumentException e) {
            send(exchange, 400, HtmlPages.clients(dbService.getAllClients(), session.csrfToken(), e.getMessage(), name, street, phones, false));

            return;
        }

        redirect(exchange, "/clients?created=1");
    }

    private void logout(HttpExchange exchange) throws IOException {
        requireMethod(exchange, "POST");
        SessionManager.Session session = currentSession(exchange);

        if (session == null) {
            redirect(exchange, "/");
            return;
        }

        requireCsrf(session, readForm(exchange));
        sessions.remove(session.id());
        exchange.getResponseHeaders().add("Set-Cookie", COOKIE_NAME + "=; Path=/; HttpOnly; SameSite=Lax; Max-Age=0");
        redirect(exchange, "/");
    }

    private SessionManager.Session currentSession(HttpExchange exchange) {
        return sessions.find(sessionId(exchange));
    }

    private static String sessionId(HttpExchange exchange) {
        List<String> headers = exchange.getRequestHeaders().get("Cookie");
        if (headers != null) {
            for (String header : headers) {
                for (String cookie : header.split(";")) {
                    String[] pair = cookie.strip().split("=", 2);
                    if (pair.length == 2 && COOKIE_NAME.equals(pair[0])) {
                        return pair[1];
                    }
                }
            }
        }

        return null;
    }

    private static void requireCsrf(SessionManager.Session session, Map<String, String> form) {
        if (!secureEquals(session.csrfToken(), form.get("csrf"))) {
            throw new HttpError(403, "Недействительный токен формы. Обновите страницу.");
        }
    }

    private static boolean secureEquals(String expected, String actual) {
        return actual != null && MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }

    private static void requireMethod(HttpExchange exchange, String... allowed) {
        for (String method : allowed) {
            if (method.equals(exchange.getRequestMethod())) {
                return;
            }
        }

        exchange.getResponseHeaders().set("Allow", String.join(", ", allowed));

        throw new HttpError(405, "Метод не поддерживается");
    }

    private static Map<String, String> readForm(HttpExchange exchange) throws IOException {
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.split(";", 2)[0].strip().equalsIgnoreCase("application/x-www-form-urlencoded")) {
            throw new HttpError(415, "Ожидается HTML-форма");
        }

        byte[] bytes = exchange.getRequestBody().readNBytes(MAX_FORM_BYTES + 1);

        if (bytes.length > MAX_FORM_BYTES) {
            throw new HttpError(413, "Форма слишком большая");
        }

        Map<String, String> fields = new HashMap<>();
        String body = new String(bytes, StandardCharsets.UTF_8);
        if (body.isEmpty()) {
            return fields;
        }

        try {
            for (String entry : body.split("&")) {
                String[] pair = entry.split("=", 2);
                String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                String value = pair.length == 2 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "";
                if (fields.putIfAbsent(key, value) != null) {
                    throw new HttpError(400, "Повторяющееся поле формы");
                }
            }
        } catch (IllegalArgumentException e) {
            throw new HttpError(400, "Некорректная кодировка формы");
        }

        return fields;
    }

    private static void redirect(HttpExchange exchange, String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(303, -1);
    }

    private static void error(HttpExchange exchange, int status, String message) throws IOException {
        send(exchange, status, "<!doctype html><html lang=\"ru\"><meta charset=\"UTF-8\">" + "<title>Ошибка</title><h1>" + status + "</h1><p>" + message + "</p><a href=\"/\">На главную</a></html>");
    }

    private static void send(HttpExchange exchange, int status, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
    }

    @Override
    public void close() {
        server.stop(1);
        executor.close();
    }

    private static final class HttpError extends RuntimeException {
        private final int status;

        private HttpError(int status, String message) {
            super(message);
            this.status = status;
        }
    }
}