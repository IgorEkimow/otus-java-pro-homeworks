package ru.otus.java.pro.homeworks.homework16.api;

import ru.otus.java.pro.homeworks.homework16.api.model.SensorData;

public interface SensorDataProcessor {
    void process(SensorData data);
    default void onProcessingEnd() {}
}