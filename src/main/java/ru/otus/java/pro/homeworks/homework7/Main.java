package ru.otus.java.pro.homeworks.homework7;

public class Main {
    public static void main(String[] args) {
        ATM atm1 = new ATM("A-001", 10000.0);
        ATM atm2 = new ATM("A-002", 5000.0);
        ATM atm3 = new ATM("A-003", 20000.0);

        Department central = new Department("Центральный офис");
        central.addATM(atm1);
        central.addATM(atm2);
        central.addATM(atm3);
        System.out.println("--- Начальное состояние ---");
        System.out.println("ATM1: " + atm1.getBalance());
        System.out.println("ATM2: " + atm2.getBalance());
        System.out.println("ATM3: " + atm3.getBalance());
        System.out.println("Общий остаток в департаменте: " + central.getBalance());
        System.out.println("\n--- Происходит снятие наличных ---");
        atm1.withdraw(2000);
        atm2.withdraw(1000);
        atm3.withdraw(15000);
        System.out.println("\n--- Состояние после операций ---");
        System.out.println("ATM1: " + atm1.getBalance());
        System.out.println("ATM2: " + atm2.getBalance());
        System.out.println("ATM3: " + atm3.getBalance());
        System.out.println("Общий остаток в департаменте: " + central.getBalance());
        System.out.println("\n--- Восстановление состояния (Memento) ---");
        central.restoreState();
        System.out.println("\n--- Итоговое состояние ---");
        System.out.println("ATM1: " + atm1.getBalance());
        System.out.println("ATM2: " + atm2.getBalance());
        System.out.println("ATM3: " + atm3.getBalance());
        System.out.println("Общий остаток в департаменте: " + central.getBalance());
    }
}