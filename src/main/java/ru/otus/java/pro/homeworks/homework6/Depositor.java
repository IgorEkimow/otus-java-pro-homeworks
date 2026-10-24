package ru.otus.java.pro.homeworks.homework6;

import java.util.Map;

public interface Depositor {
    void deposit(Map<Denomination, Integer> banknotes);
}