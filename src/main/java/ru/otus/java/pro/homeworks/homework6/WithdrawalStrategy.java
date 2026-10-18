package ru.otus.java.pro.homeworks.homework6;

import java.util.Map;

public interface WithdrawalStrategy {
    Map<Denomination, Integer> plan(int amount, Map<Denomination, Integer> cellSnapshot);
}