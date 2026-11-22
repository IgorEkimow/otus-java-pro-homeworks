package ru.otus.java.pro.homeworks.homework11.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.otus.java.pro.homeworks.homework11.model.Address;
import ru.otus.java.pro.homeworks.homework11.model.Client;
import ru.otus.java.pro.homeworks.homework11.model.Phone;

public final class HibernateUtils {
    private HibernateUtils() {}

    public static SessionFactory createSessionFactory(String databaseName) {
        return new Configuration()
            .addAnnotatedClass(Client.class)
            .addAnnotatedClass(Address.class)
            .addAnnotatedClass(Phone.class)
            .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
            .setProperty("hibernate.connection.url", "jdbc:h2:mem:" + databaseName)
            .setProperty("hibernate.connection.username", "sa")
            .setProperty("hibernate.connection.password", "")
            .setProperty("hibernate.hbm2ddl.auto", "create-drop")
            .setProperty("hibernate.show_sql", "false")
            .setProperty("hibernate.generate_statistics", "true")
            .setProperty("hibernate.session.events.log", "false")
            .setProperty("hibernate.cache.use_second_level_cache", "false")
            .setProperty("hibernate.cache.use_query_cache", "false")
            .buildSessionFactory();
    }
}