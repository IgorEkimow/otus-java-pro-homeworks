package ru.otus.java.pro.homeworks.homework7;

public class ATMMemento {
    private final String atmId;
    private final double initialBalance;

    public ATMMemento(String atmId, double initialBalance) {
        this.atmId = atmId;
        this.initialBalance = initialBalance;
    }

    public String getAtmId() {
        return atmId;
    }

    public double getInitialBalance() {
        return initialBalance;
    }
}