package ru.otus.java.pro.homeworks.homework6;

import java.util.EnumMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Atm atm = new Atm();

        Map<Denomination, Integer> cash = new EnumMap<>(Denomination.class);
        cash.put(Denomination.THOUSAND, 10);
        cash.put(Denomination.FIVE_HUNDRED, 20);
        cash.put(Denomination.HUNDRED, 50);
        cash.put(Denomination.FIFTY, 30);
        atm.deposit(cash);

        System.out.println("Баланс: " + atm.balance());

        Map<Denomination, Integer> out = atm.withdraw(7300);

        System.out.println("Выдано: " + out);
        System.out.println("Баланс после выдачи: " + atm.balance());

        try {
            atm.withdraw(123);
        } catch (CannotWithdrawException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        try {
            atm.withdraw(1_000_000);
        } catch (CannotWithdrawException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}