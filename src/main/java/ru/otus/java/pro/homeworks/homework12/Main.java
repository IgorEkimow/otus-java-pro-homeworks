package ru.otus.java.pro.homeworks.homework12;

import ru.otus.java.pro.homeworks.homework12.service.DbService;
import ru.otus.java.pro.homeworks.homework12.util.HibernateUtils;
import ru.otus.java.pro.homeworks.homework12.web.ClientsWebServer;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        String login = System.getenv().getOrDefault("ADMIN_LOGIN", "admin");
        String password = System.getenv().getOrDefault("ADMIN_PASSWORD", "admin");
        String jdbcUrl = System.getenv().getOrDefault("JDBC_URL", "jdbc:h2:./data/homework12/clients");
        Files.createDirectories(Path.of("data", "homework12"));

        var sessionFactory = HibernateUtils.createSessionFactory(jdbcUrl);

        try {
            var server = new ClientsWebServer(new InetSocketAddress("127.0.0.1", port), new DbService(sessionFactory), login, password);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.close();
                sessionFactory.close();
            }, "homework12-shutdown"));

            server.start();

            System.out.printf("Сервер запущен: http://127.0.0.1:%d%n", server.getPort());
            System.out.println("Для остановки нажмите Ctrl+C.");
        } catch (Exception e) {
            sessionFactory.close();

            throw e;
        }
    }
}