package ru.otus.java.pro.homeworks.homework12.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import ru.otus.java.pro.homeworks.homework12.model.Address;
import ru.otus.java.pro.homeworks.homework12.model.Client;
import ru.otus.java.pro.homeworks.homework12.model.Phone;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DbService {
    private static final int MAX_FIELD_LENGTH = 255;
    private static final int MAX_PHONES = 20;
    private final SessionFactory sessionFactory;

    public DbService(SessionFactory sessionFactory) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory, "sessionFactory");
    }

    public Long createClient(String name, String street, String phones) {
        String normalizedName = normalize(name);
        String normalizedStreet = normalize(street);

        if (normalizedName.isEmpty()) {
            throw new IllegalArgumentException("Укажите имя клиента.");
        }

        checkLength(normalizedName, "Имя клиента");
        checkLength(normalizedStreet, "Адрес");
        List<String> phoneNumbers = normalize(phones).lines().map(String::strip).filter(number -> !number.isEmpty()).toList();

        if (phoneNumbers.size() > MAX_PHONES) {
            throw new IllegalArgumentException("Можно указать не более 20 телефонов.");
        }

        phoneNumbers.forEach(number -> checkLength(number, "Номер телефона"));

        Client client = new Client(normalizedName);
        if (!normalizedStreet.isEmpty()) {
            client.setAddress(new Address(normalizedStreet));
        }

        phoneNumbers.forEach(number -> client.addPhone(new Phone(number)));

        return saveClient(client);
    }

    public Long saveClient(Client client) {
        Objects.requireNonNull(client, "client");
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.persist(client);
                tx.commit();

                return client.getId();
            } catch (RuntimeException exception) {
                if (tx.isActive()) {
                    tx.rollback();
                }

                throw exception;
            }
        }
    }

    public Optional<Client> getClient(Long id) {
        Objects.requireNonNull(id, "id");
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select distinct c from Client c " + "left join fetch c.address left join fetch c.phones where c.id = :id", Client.class).setParameter("id", id).uniqueResultOptional();
        }
    }

    public List<Client> getAllClients() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select distinct c from Client c " + "left join fetch c.address left join fetch c.phones order by c.id", Client.class).getResultList();
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.strip();
    }

    private static void checkLength(String value, String field) {
        if (value.length() > MAX_FIELD_LENGTH) {
            throw new IllegalArgumentException(field + " — не более 255 символов.");
        }
    }
}