package ru.otus.java.pro.homeworks.homework7;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Department implements ATMComponent {
    private final String name;
    private final List<ATMComponent> atms = new ArrayList<>();

    public Department(String name) {
        this.name = name;
    }

    public void addATM(ATMComponent atm) {
        atms.add(atm);
    }

    public void removeATM(ATMComponent atm) {
        atms.remove(atm);
    }

    public String getName() {
        return name;
    }

    @Override
    public double getBalance() {
        double total = 0;
        Iterator<ATMComponent> iterator = atms.iterator();
        while (iterator.hasNext()) {
            ATMComponent component = iterator.next();
            total += component.getBalance();
        }

        return total;
    }

    @Override
    public void restoreState() {
        System.out.println("=== Департамент \"" + name + "\" инициирует восстановление всех ATM ===");
        for (ATMComponent component : atms) {
            component.restoreState();
        }
    }

    @Override
    public String toString() {
        return "Department{name='" + name + "', atms=" + atms + "}";
    }
}