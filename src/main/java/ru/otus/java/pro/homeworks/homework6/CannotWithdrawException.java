package ru.otus.java.pro.homeworks.homework6;

public class CannotWithdrawException extends RuntimeException {
    public CannotWithdrawException(String message) {
        super(message);
    }
}