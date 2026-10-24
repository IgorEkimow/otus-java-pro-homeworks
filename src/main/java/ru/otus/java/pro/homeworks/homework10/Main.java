package ru.otus.java.pro.homeworks.homework10;

import ru.otus.java.pro.homeworks.homework10.model.Address;
import ru.otus.java.pro.homeworks.homework10.model.Client;
import ru.otus.java.pro.homeworks.homework10.model.Phone;
import ru.otus.java.pro.homeworks.homework10.service.DbService;
import ru.otus.java.pro.homeworks.homework10.util.HibernateUtils;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DbService dbService = new DbService();

        try {
            Client client = new Client("Ivan Petrov");
            Address address = new Address("Lenina street, 1");

            client.setAddress(address);
            client.addPhone(new Phone("+7-999-000-00-01"));
            client.addPhone(new Phone("+7-999-000-00-02"));
            client.addPhone(new Phone("+7-999-000-00-03"));

            System.out.println(">>> Сохраняем клиента " + "(ожидаем 1 INSERT client, 1 INSERT address, 3 INSERT phone, БЕЗ UPDATE)");
            Long id = dbService.saveClient(client);
            System.out.println(">>> Сохранён клиент id = " + id);

            System.out.println("\n>>> Читаем клиента по id");
            dbService.getClient(id).ifPresentOrElse(c -> System.out.println("Прочитан: " + c + "\n   Адрес: " + c.getAddress() + "\n   Телефоны: " + c.getPhones()), () -> System.out.println("Клиент не найден"));

            System.out.println("\n>>> Все клиенты");
            List<Client> all = dbService.getAllClients();
            all.forEach(System.out::println);
        } finally {
            HibernateUtils.shutdown();
        }
    }
}