package ru.otus.java.pro.homeworks.homework16.lib;

import java.util.List;
import ru.otus.java.pro.homeworks.homework16.api.model.SensorData;

public interface SensorDataBufferedWriter {
    void writeBufferedData(List<SensorData> bufferedData);
}