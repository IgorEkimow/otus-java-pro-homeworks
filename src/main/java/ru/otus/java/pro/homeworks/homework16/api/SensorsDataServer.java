package ru.otus.java.pro.homeworks.homework16.api;

import ru.otus.java.pro.homeworks.homework16.api.model.SensorData;

public interface SensorsDataServer {
    void onReceive(SensorData sensorData);
}