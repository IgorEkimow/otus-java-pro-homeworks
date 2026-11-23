package ru.otus.java.pro.homeworks.homework14.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(long id) {
        super("Клиент с id " + id + " не найден.");
    }
}