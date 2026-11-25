package ru.otus.java.pro.homeworks.homework16.services.processors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.pro.homeworks.homework16.api.SensorDataProcessor;
import ru.otus.java.pro.homeworks.homework16.api.model.SensorData;
import ru.otus.java.pro.homeworks.homework16.lib.SensorDataBufferedWriter;

public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);
    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final PriorityQueue<SensorData> dataBuffer = new PriorityQueue<>(Comparator.comparing(SensorData::getMeasurementTime));

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Размер буфера должен быть больше нуля");
        }

        this.bufferSize = bufferSize;
        this.writer = Objects.requireNonNull(writer, "writer");
    }

    @Override
    public synchronized void process(SensorData data) {
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(data.getMeasurementTime(), "measurementTime");
        dataBuffer.add(data);

        if (dataBuffer.size() >= bufferSize) {
            flush();
        }
    }

    public synchronized void flush() {
        if (dataBuffer.isEmpty()) {
            return;
        }

        var snapshot = new PriorityQueue<>(dataBuffer);
        var bufferedData = new ArrayList<SensorData>(snapshot.size());
        while (!snapshot.isEmpty()) {
            bufferedData.add(snapshot.poll());
        }

        try {
            writer.writeBufferedData(bufferedData);
            dataBuffer.clear();
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}