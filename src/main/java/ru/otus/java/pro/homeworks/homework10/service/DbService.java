package ru.otus.java.pro.homeworks.homework10.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.otus.java.pro.homeworks.homework10.model.Client;
import ru.otus.java.pro.homeworks.homework10.util.HibernateUtils;
import java.util.List;
import java.util.Optional;

public class DbService {
    private final SessionFactory sessionFactory;

    public DbService() {
        this.sessionFactory = HibernateUtils.getSessionFactory();
    }

    public Long saveClient(Client client) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(client);
                tx.commit();

                return client.getId();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }

                throw e;
            }
        }
    }

    public Optional<Client> getClient(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Client client = session.get(Client.class, id);
            if (client == null) {
                return Optional.empty();
            }

            if (client.getAddress() != null) {
                client.getAddress().getStreet();
            }

            client.getPhones().size();

            return Optional.of(client);
        }
    }

    public List<Client> getAllClients() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select distinct c from Client c " + "left join fetch c.address " + "left join fetch c.phones", Client.class).getResultList();
        }
    }
}