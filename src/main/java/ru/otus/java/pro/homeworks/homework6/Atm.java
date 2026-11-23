package ru.otus.java.pro.homeworks.homework6;

import java.util.*;

public class Atm implements Depositor, Dispenser, BalanceProvider {
    private final Map<Denomination, Cell> cells = new EnumMap<>(Denomination.class);
    private final WithdrawalStrategy strategy;

    public Atm(WithdrawalStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
        for (Denomination d : Denomination.values()) {
            cells.put(d, new Cell(d));
        }
    }

    public Atm() {
        this(new MinimalBanknotesStrategy());
    }

    @Override
    public void deposit(Map<Denomination, Integer> banknotes) {
        Objects.requireNonNull(banknotes);
        for (var entry : banknotes.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("Номинал не задан");
            }

            if (entry.getValue() == null || entry.getValue() < 0) {
                throw new IllegalArgumentException("Количество не может быть отрицательным");
            }
        }

        banknotes.forEach((denomination, count) -> cells.get(denomination).add(count));
    }

    @Override
    public Map<Denomination, Integer> withdraw(int amount) {
        if (amount <= 0) {
            throw new CannotWithdrawException("Сумма должна быть положительной");
        }

        if (amount > balance()) {
            throw new CannotWithdrawException("Недостаточно средств: запрошено " + amount + ", в банкомате " + balance());
        }

        Map<Denomination, Integer> snapshot = snapshot();
        Map<Denomination, Integer> plan = strategy.plan(amount, snapshot);
        plan.forEach((d, n) -> cells.get(d).remove(n));

        return plan;
    }

    @Override
    public int balance() {
        return cells.values().stream().mapToInt(Cell::total).sum();
    }

    public Map<Denomination, Integer> snapshot() {
        Map<Denomination, Integer> result = new EnumMap<>(Denomination.class);
        cells.forEach((d, cell) -> result.put(d, cell.count()));

        return result;
    }
}