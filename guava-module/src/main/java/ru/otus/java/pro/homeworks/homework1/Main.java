package ru.otus.java.pro.homeworks.homework1;

import java.util.List;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class Main {
    public static void main(String[] args) {
        List<String> words = List.of("Hello", "Otus", "from", "Guava");
        HelloOtus helloOtus = new HelloOtus();
        List<String> unmodifiableList = helloOtus.unmodifiableList(words);
        System.out.println(Joiner.on(' ').join(unmodifiableList));
        System.out.println("Partitions: " + Lists.partition(unmodifiableList, 2));
    }
}