package ru.otus.java.pro.homeworks.homework12.web;

import ru.otus.java.pro.homeworks.homework12.model.Client;
import ru.otus.java.pro.homeworks.homework12.model.Phone;
import java.util.List;

public final class HtmlPages {
    private static final String CSS = """
        :root { color-scheme: light; font-family: system-ui, -apple-system, sans-serif;
            color: #15253c; background: #f3f6fb; line-height: 1.5; }
        * { box-sizing: border-box; }
        body { margin: 0; }
        a { color: #2455c6; text-underline-offset: 3px; }
        button, input, textarea { font: inherit; }
        button, .button { display: inline-flex; align-items: center; justify-content: center;
            min-height: 44px; padding: 10px 18px; border: 1px solid transparent; border-radius: 9px;
            background: #2455c6; color: white; font-weight: 650; cursor: pointer; text-decoration: none; }
        button:hover, .button:hover { background: #1c439f; }
        .secondary { color: #253957; background: white; border-color: #cbd5e3; }
        .secondary:hover { background: #eef3fb; }
        :focus-visible { outline: 3px solid #93b8ff; outline-offset: 3px; }
        .shell { width: min(1180px, calc(100% - 48px)); margin: 0 auto; }
        .topbar { background: white; border-bottom: 1px solid #dce4ef; }
        .topbar .shell { display: flex; gap: 20px; align-items: center;
            justify-content: space-between; padding-top: 18px; padding-bottom: 18px; }
        .brand { display: flex; align-items: center; gap: 12px; font-size: 17px; font-weight: 750; }
        .brand-mark { display: grid; place-items: center; width: 36px; height: 36px;
            background: #2455c6; color: white; border-radius: 10px; font-size: 20px; }
        main { padding: 38px 0 48px; }
        h1, h2, p { margin-top: 0; }
        h1 { margin-bottom: 8px; font-size: clamp(28px, 4vw, 38px); line-height: 1.2; letter-spacing: -.03em; }
        h2 { margin-bottom: 8px; font-size: 21px; line-height: 1.3; }
        .muted { color: #586980; }
        .intro { margin-bottom: 28px; }
        .eyebrow { color: #2455c6; font-size: 12px; font-weight: 750;
            letter-spacing: .09em; text-transform: uppercase; margin-bottom: 10px; }
        .grid { display: grid; grid-template-columns: 340px minmax(0, 1fr); gap: 24px; align-items: start; }
        .card { background: white; border: 1px solid #dce4ef; border-radius: 14px;
            box-shadow: 0 4px 16px #172c4a05; padding: 26px; }
        .field { margin-bottom: 18px; }
        label { display: block; margin-bottom: 6px; font-size: 14px; font-weight: 650; }
        .optional { color: #65748a; font-weight: 400; }
        input, textarea { display: block; width: 100%; padding: 10px 12px; border: 1px solid #b8c6d9;
            border-radius: 8px; color: #15253c; background: white; }
        textarea { resize: vertical; min-height: 108px; }
        .hint { display: block; margin-top: 5px; font-size: 12px; color: #586980; }
        .wide { width: 100%; }
        .notice { padding: 12px 15px; border-radius: 9px; margin-bottom: 22px; overflow-wrap: anywhere; }
        .error { background: #fff1f1; color: #982d35; border: 1px solid #f0c6ca; }
        .success { background: #edf8f2; color: #1d6547; border: 1px solid #b9ddcc; }
        .list-header { display: flex; gap: 16px; justify-content: space-between; align-items: center; margin-bottom: 20px; }
        .list-header h2 { margin-bottom: 0; }
        .count { display: inline-block; margin-left: 7px; padding: 2px 8px; border-radius: 6px;
            background: #edf2fb; color: #3a547a; font-size: 13px; vertical-align: middle; }
        .table-wrap { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; font-size: 14px; }
        th { text-align: left; font-size: 12px; font-weight: 650; color: #586980; background: #f7f9fc; }
        th, td { padding: 13px 12px; border-bottom: 1px solid #e6ecf4; vertical-align: top; }
        td { overflow-wrap: anywhere; min-width: 100px; }
        td:first-child { min-width: 48px; color: #586980; }
        tbody tr:last-child td { border-bottom: 0; }
        .phone-list { list-style: none; padding: 0; margin: 0; }
        .phone-list li + li { margin-top: 4px; }
        .empty { text-align: center; padding: 40px 16px; color: #586980; }
        .empty strong { display: block; margin-bottom: 6px; color: #253957; }
        .login { width: min(440px, calc(100% - 40px)); margin: 64px auto; }
        .login h1 { font-size: 28px; }
        .login .card { margin-top: 26px; padding: 30px; }
        .login .lead { margin-bottom: 24px; }
        .login .footnote { margin: 20px 0 0; text-align: center; font-size: 13px; }
        @media (max-width: 900px) { .grid { grid-template-columns: 1fr; }
            .grid > .card:first-child { max-width: none; } }
        @media (max-width: 540px) { .shell { width: calc(100% - 28px); }
            .card { padding: 19px; } main { padding-top: 28px; }
            .brand { font-size: 15px; } .list-header { align-items: flex-start; flex-wrap: wrap; }
            .login { margin-top: 32px; } .login .card { padding: 23px; } }
    """;

    private HtmlPages() {}

    public static String login(String error) {
        String body = """
            <header class="topbar"><div class="shell">
              <div class="brand"><span class="brand-mark" aria-hidden="true">К</span>Клиенты</div>
              <span class="muted">Администрирование</span>
            </div></header>
            <main class="login">
              <p class="eyebrow">Работа с клиентами</p>
              <h1>Вход для администратора</h1>
              <p class="muted">Войдите, чтобы добавлять клиентов и просматривать их данные.</p>
              <section class="card" aria-label="Вход">
            """ + errorNotice(error) + """
                <form action="/login" method="post">
                  <div class="field">
                    <label for="username">Логин</label>
                    <input id="username" name="username" autocomplete="username" required autofocus>
                  </div>
                  <div class="field">
                    <label for="password">Пароль</label>
                    <input id="password" name="password" type="password" autocomplete="current-password" required>
                  </div>
                  <button class="wide" type="submit">Войти</button>
                </form>
              </section>
            </main>
        """;

        return page("Вход — Клиенты", body);
    }

    public static String clients(List<Client> clients, String csrfToken, String error, String name, String street, String phones, boolean created) {
        StringBuilder body = new StringBuilder("""
            <header class="topbar"><div class="shell">
              <div class="brand"><span class="brand-mark" aria-hidden="true">К</span>Клиенты</div>
              <form action="/logout" method="post">
        """);

        body.append(csrfInput(csrfToken)).append("""
                <button class="secondary" type="submit">Выйти</button>
              </form>
            </div></header>
            <main class="shell">
              <div class="intro">
                <p class="eyebrow">Панель администратора</p>
                <h1>Управление клиентами</h1>
                <p class="muted">Добавляйте клиентов и находите их контактные данные в общем списке.</p>
              </div>
        """);

        if (created) {
            body.append("<div class=\"notice success\" role=\"status\">Клиент успешно создан.</div>");
        }

        body.append(errorNotice(error)).append("""
          <div class="grid">
            <section class="card" aria-labelledby="create-heading">
              <h2 id="create-heading">Новый клиент</h2>
              <p class="muted">Укажите имя. Адрес и телефоны можно добавить по желанию.</p>
              <form action="/clients" method="post">
        """);

        body.append(csrfInput(csrfToken)).append("""
            <div class="field">
              <label for="name">Имя клиента</label>
              <input id="name" name="name" autocomplete="name" required maxlength="255" value="""
        ).append('"').append(escape(name)).append("\">\n").append("""
            </div>
            <div class="field">
              <label for="street">Адрес <span class="optional">· необязательно</span></label>
              <input id="street" name="street" autocomplete="street-address" maxlength="255" value="""
        ).append('"').append(escape(street)).append("\">\n").append("""
            </div>
            <div class="field">
              <label for="phones">Телефоны <span class="optional">· необязательно</span></label>
              <textarea id="phones" name="phones" rows="3" maxlength="5119" aria-describedby="phones-hint">""")
        .append(escape(phones)).append("""
            </textarea>
              <small class="hint" id="phones-hint">Каждый номер с новой строки, не более 20 номеров.</small>
            </div>
            <button class="wide" type="submit">Создать клиента</button>
          </form>
        </section>
        <section class="card" aria-labelledby="list-heading">
          <div class="list-header">
            <h2 id="list-heading">Список клиентов <span class="count">""")
        .append(clients.size()).append("""
            </span></h2>
                <a class="button secondary" href="/clients">Обновить список</a>
              </div>
              <div class="table-wrap">
                <table aria-labelledby="list-heading">
                  <thead><tr><th scope="col">ID</th><th scope="col">Имя</th>
                    <th scope="col">Адрес</th><th scope="col">Телефоны</th></tr></thead>
                  <tbody>
        """);

        if (clients.isEmpty()) {
            body.append("""
                <tr><td class="empty" colspan="4"><strong>Клиентов пока нет</strong>
                Заполните форму, чтобы добавить первого клиента.</td></tr>
            """);
        } else {
            for (Client client : clients) {
                body.append("<tr><td>").append(client.getId()).append("</td><td>").append(escape(client.getName())).append("</td><td>").append(client.getAddress() == null ? "—" : escape(client.getAddress().getStreet())).append("</td><td>");
                if (client.getPhones().isEmpty()) {
                    body.append("—");
                } else {
                    body.append("<ul class=\"phone-list\">");
                    for (Phone phone : client.getPhones()) {
                        body.append("<li>").append(escape(phone.getNumber())).append("</li>");
                    }

                    body.append("</ul>");
                }

                body.append("</td></tr>\n");
            }
        }

        body.append("""
                      </tbody>
                    </table>
                  </div>
                </section>
              </div>
            </main>
        """);

        return page("Управление клиентами", body.toString());
    }

    private static String csrfInput(String token) {
        return "<input type=\"hidden\" name=\"csrf\" value=\"" + escape(token) + "\">\n";
    }

    private static String errorNotice(String error) {
        return error == null || error.isBlank() ? "" : "<div class=\"notice error\" role=\"alert\">" + escape(error) + "</div>\n";
    }

    private static String page(String title, String body) {
        return "<!doctype html>\n<html lang=\"ru\">\n<head>\n<meta charset=\"UTF-8\">\n"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
            + "<title>" + escape(title) + "</title>\n<style>\n" + CSS + "</style>\n</head>\n<body>\n"
            + body + "</body>\n</html>\n";
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
}