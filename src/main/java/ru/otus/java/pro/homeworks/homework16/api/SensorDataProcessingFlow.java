package ru.otus.java.pro.homeworks.homework16.api;

public interface SensorDataProcessingFlow {
    void startProcessing();
    void stopProcessing();
    void bindProcessor(String roomPattern, SensorDataProcessor processor);
}