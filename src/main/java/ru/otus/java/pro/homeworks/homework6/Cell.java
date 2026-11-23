package ru.otus.java.pro.homeworks.homework6;

import java.util.Objects;

public class Cell {
    private final Denomination denomination;
    private int count;

    public Cell(Denomination denomination) {
        this.denomination = Objects.requireNonNull(denomination);
    }

    public Denomination denomination() {
        return denomination;
    }

    public int count() {
        return count;
    }

    public int total() {
        return count * denomination.value();
    }

    public void add(int banknotes) {
        if (banknotes < 0) {
            throw new IllegalArgumentException("Количество не может быть отрицательным");
        }

        count += banknotes;
    }

    public void remove(int banknotes) {
        if (banknotes < 0 || banknotes > count) {
            throw new IllegalArgumentException("Недостаточно банкнот в ячейке");
        }

        count -= banknotes;
    }

    public boolean canWithdraw(int banknotes) {
        return banknotes >= 0 && banknotes <= count;
    }
}