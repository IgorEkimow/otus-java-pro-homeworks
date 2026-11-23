package ru.otus.java.pro.homeworks.homework14.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.java.pro.homeworks.homework14.model.Address;
import ru.otus.java.pro.homeworks.homework14.model.Client;
import ru.otus.java.pro.homeworks.homework14.model.Phone;
import ru.otus.java.pro.homeworks.homework14.repository.ClientRepository;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClientService {
    private static final int MAX_FIELD_LENGTH = 255;
    private static final int MAX_PHONES = 20;
    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    public List<Client> findAll() {
        return repository.findAllByOrderByIdAsc();
    }

    public Client getById(long id) {
        return repository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }

    @Transactional
    public Client create(String name, String street, String phones) {
        ClientData data = validate(name, street, phones);

        return repository.save(new Client(data.name(), data.address(), data.phones()));
    }

    @Transactional
    public Client update(long id, String name, String street, String phones) {
        Client client = getById(id);
        ClientData data = validate(name, street, phones);
        client.setName(data.name());
        client.setAddress(data.address());
        client.setPhones(data.phones());

        return repository.save(client);
    }

    @Transactional
    public void delete(long id) {
        repository.delete(getById(id));
    }

    private static ClientData validate(String name, String street, String phones) {
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
        Address address = normalizedStreet.isEmpty() ? null : new Address(normalizedStreet);

        return new ClientData(normalizedName, address, phoneNumbers.stream().map(Phone::new).toList());
    }

    private static String normalize(String value) {
        return value == null ? "" : value.strip();
    }

    private static void checkLength(String value, String field) {
        if (value.length() > MAX_FIELD_LENGTH) {
            throw new IllegalArgumentException(field + " — не более 255 символов.");
        }
    }

    private record ClientData(String name, Address address, List<Phone> phones) {}
}