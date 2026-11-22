package ru.otus.java.pro.homeworks.homework14.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import ru.otus.java.pro.homeworks.homework14.model.Client;
import ru.otus.java.pro.homeworks.homework14.model.Phone;
import java.util.stream.Collectors;

public class ClientForm {
    @NotBlank(message = "Укажите имя клиента.")
    @Size(max = 255, message = "Имя клиента — не более 255 символов.")
    private String name = "";

    @Size(max = 255, message = "Адрес — не более 255 символов.")
    private String street = "";

    @Size(max = 5119, message = "Список телефонов слишком длинный.")
    private String phones = "";

    public static ClientForm from(Client client) {
        ClientForm form = new ClientForm();
        form.setName(client.getName());
        form.setStreet(client.getAddress() == null ? "" : client.getAddress().getStreet());
        form.setPhones(client.getPhones().stream().map(Phone::getNumber).collect(Collectors.joining("\n")));

        return form;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPhones() {
        return phones;
    }

    public void setPhones(String phones) {
        this.phones = phones == null ? null : phones.replace("\r\n", "\n").replace('\r', '\n');
    }
}