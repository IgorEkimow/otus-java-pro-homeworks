package ru.otus.java.pro.homeworks.homework11;

public interface HwListener<K, V> {
    void notify(K key, V value, String action);
}