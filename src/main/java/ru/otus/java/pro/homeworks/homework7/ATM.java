package ru.otus.java.pro.homeworks.homework7;

public class ATM implements ATMComponent {
    private final String id;
    private double currentBalance;
    private final ATMMemento memento;

    public ATM(String id, double initialBalance) {
        this.id = id;
        this.currentBalance = initialBalance;
        this.memento = new ATMMemento(id, initialBalance);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("ATM " + id + ": Некорректная сумма снятия.");

            return;
        }

        if (currentBalance >= amount) {
            currentBalance -= amount;
            System.out.println("ATM " + id + ": Снято " + amount + ". Текущий остаток: " + currentBalance);
        } else {
            System.out.println("ATM " + id + ": Недостаточно средств. " + "Запрошено: " + amount + ", доступно: " + currentBalance);
        }
    }

    @Override
    public double getBalance() {
        return currentBalance;
    }

    @Override
    public void restoreState() {
        this.currentBalance = memento.getInitialBalance();
        System.out.println("ATM " + id + ": Состояние восстановлено до " + currentBalance);
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ATM{id='" + id + "', balance=" + currentBalance + "}";
    }
}