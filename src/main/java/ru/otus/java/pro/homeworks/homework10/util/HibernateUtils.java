package ru.otus.java.pro.homeworks.homework10.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public final class HibernateUtils {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtils() {}

    private static SessionFactory buildSessionFactory() {
        try {
            File configFile = new File("src/main/resources/hibernate.cfg.xml");
            return new Configuration().configure(configFile).buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Initial SessionFactory creation failed: " + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null) {
            SESSION_FACTORY.close();
        }
    }
}