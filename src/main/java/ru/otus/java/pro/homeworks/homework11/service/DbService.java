package ru.otus.java.pro.homeworks.homework11.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.otus.java.pro.homeworks.homework11.HwCache;
import ru.otus.java.pro.homeworks.homework11.model.Client;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DbService {
    private final SessionFactory sessionFactory;
    private final HwCache<String, Client> cache;

    public DbService(SessionFactory sessionFactory) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
        this.cache = null;
    }

    public DbService(SessionFactory sessionFactory, HwCache<String, Client> cache) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
        this.cache = Objects.requireNonNull(cache);
    }

    public Long saveClient(Client client) {
        Objects.requireNonNull(client);
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(client);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx.isActive()) {
                    tx.rollback();
                }

                throw e;
            }
        }

        if (cache != null) {
            cache.remove(client.getId().toString());
        }

        return client.getId();
    }

    public Optional<Client> getClient(Long id) {
        Objects.requireNonNull(id);
        String key = id.toString();

        if (cache != null) {
            Client cached = cache.get(key);
            if (cached != null) {
                return Optional.of(cached);
            }
        }

        try (Session session = sessionFactory.openSession()) {
            Client client = session.find(Client.class, id);
            if (client == null) {
                return Optional.empty();
            }

            if (client.getAddress() != null) {
                client.getAddress().getStreet();
            }

            client.getPhones().size();

            if (cache != null) {
                cache.put(key, client);
            }

            return Optional.of(client);
        }
    }

    public List<Client> getAllClients() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select distinct c from Client c " + "left join fetch c.address left join fetch c.phones", Client.class).getResultList();
        }
    }
}