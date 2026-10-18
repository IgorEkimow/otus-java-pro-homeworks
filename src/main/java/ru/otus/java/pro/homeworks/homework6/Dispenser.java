package ru.otus.java.pro.homeworks.homework6;

import java.util.Map;

public interface Dispenser {
    Map<Denomination, Integer> withdraw(int amount);
}