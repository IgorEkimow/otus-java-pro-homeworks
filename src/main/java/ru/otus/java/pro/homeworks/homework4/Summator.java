package ru.otus.java.pro.homeworks.homework4;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Summator {
    private static final int LIST_CAPACITY = 100_000;
    private int sum = 0;
    private int prevValue = 0;
    private int prevPrevValue = 0;
    private int sumLastThreeValues = 0;
    private int someValue = 0;
    private final List<Data> listValues = new ArrayList<>(LIST_CAPACITY);
    private final SecureRandom random = new SecureRandom();

    public void calc(Data data) {
        listValues.add(data);
        if (listValues.size() % LIST_CAPACITY == 0) {
            listValues.clear();
        }

        int value = data.getValue();
        sum += value + random.nextInt();
        sumLastThreeValues = value + prevValue + prevPrevValue;
        prevPrevValue = prevValue;
        prevValue = value;

        for (var idx = 0; idx < 3; idx++) {
            someValue += (sumLastThreeValues * sumLastThreeValues / (value + 1) - sum);
            someValue = Math.abs(someValue) + listValues.size();
        }
    }

    public int getSum() {
        return sum;
    }

    public int getPrevValue() {
        return prevValue;
    }

    public int getPrevPrevValue() {
        return prevPrevValue;
    }

    public int getSumLastThreeValues() {
        return sumLastThreeValues;
    }

    public int getSomeValue() {
        return someValue;
    }
}