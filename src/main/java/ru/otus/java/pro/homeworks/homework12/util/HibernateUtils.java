package ru.otus.java.pro.homeworks.homework12.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.otus.java.pro.homeworks.homework12.model.Address;
import ru.otus.java.pro.homeworks.homework12.model.Client;
import ru.otus.java.pro.homeworks.homework12.model.Phone;
import java.util.Objects;

public final class HibernateUtils {
    private HibernateUtils() {}

    public static SessionFactory createSessionFactory(String jdbcUrl) {
        return new Configuration()
            .addAnnotatedClass(Client.class)
            .addAnnotatedClass(Address.class)
            .addAnnotatedClass(Phone.class)
            .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
            .setProperty("hibernate.connection.url", Objects.requireNonNull(jdbcUrl, "jdbcUrl"))
            .setProperty("hibernate.connection.username", "sa")
            .setProperty("hibernate.connection.password", "")
            .setProperty("hibernate.hbm2ddl.auto", "update")
            .setProperty("hibernate.show_sql", "false")
            .setProperty("hibernate.session.events.log", "false")
            .buildSessionFactory();
    }
}