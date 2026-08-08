package ru.otus.java.pro.homeworks.homework2;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n===== Задание №1 =====");
        String[] fruits = {"Персик", "Банан", "Яблоко", "Груша"};
        System.out.println("До: " + Arrays.toString(fruits));
        arraySwap(fruits, 0, 2);
        System.out.println("После: " + Arrays.toString(fruits));

        System.out.println("\n===== Задание №2 =====");
        ArrayList<String> fruitList = toArrayList(fruits);
        System.out.println("Преобразование в ArrayList: " + fruitList);

        System.out.println("\n===== Задание №3 =====");
        String[] words = {"Кот", "Птица", "Кот", "Черепаха", "Собака", "Попугай", "Рыба", "Собака", "Кот", "Птица", "Рыба", "Собака", "Кот", "Хомяк", "Рыба"};
        System.out.println("Исходный массив (" + words.length + " слов): " + Arrays.toString(words));
        Set<String> uniqueWords = new LinkedHashSet<>(Arrays.asList(words));
        System.out.println("Уникальные слова (" + uniqueWords.size() + " слов): " + uniqueWords);
        Map<String, Integer> wordCount = new LinkedHashMap<>();

        for (String word : words) {
            wordCount.merge(word, 1, Integer::sum);
        }

        System.out.println("Сколько раз встречается каждое слово:");

        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            System.out.println("  " + entry.getKey() + " — " + entry.getValue());
        }
    }

    public static <T> void arraySwap(T[] array, int a, int b) {
        if (array == null) {
            throw new IllegalArgumentException("Массив не должен быть null");
        }

        if (a < 0 || a >= array.length || b < 0 || b >= array.length) {
            throw new ArrayIndexOutOfBoundsException("Некорректные индексы: " + a + ", " + b + " (длина массива " + array.length + ")");
        }

        T tmp = array[a];
        array[a] = array[b];
        array[b] = tmp;
    }

    public static <T> ArrayList<T> toArrayList(T[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Массив не должен быть null");
        }

        ArrayList<T> list = new ArrayList<>(array.length);
        list.addAll(Arrays.asList(array));

        return list;
    }
}