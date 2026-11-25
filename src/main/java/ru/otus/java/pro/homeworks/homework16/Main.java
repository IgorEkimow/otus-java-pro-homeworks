package ru.otus.java.pro.homeworks.homework16;

import java.util.concurrent.TimeUnit;
import ru.otus.java.pro.homeworks.homework16.lib.SensorDataBufferedWriterFake;
import ru.otus.java.pro.homeworks.homework16.services.FakeSensorDataGenerator;
import ru.otus.java.pro.homeworks.homework16.services.SensorDataProcessingFlowImpl;
import ru.otus.java.pro.homeworks.homework16.services.SensorsDataQueueChannel;
import ru.otus.java.pro.homeworks.homework16.services.SensorsDataServerImpl;
import ru.otus.java.pro.homeworks.homework16.services.processors.SensorDataProcessorBuffered;
import ru.otus.java.pro.homeworks.homework16.services.processors.SensorDataProcessorCommon;
import ru.otus.java.pro.homeworks.homework16.services.processors.SensorDataProcessorErrors;
import ru.otus.java.pro.homeworks.homework16.services.processors.SensorDataProcessorRoom;

public class Main {
    private static final String ALL_ROOMS_BINDING = "*";
    private static final String ROOM_NAME_BINDING = "Комната: 4";
    private static final int BUFFER_SIZE = 15;
    private static final int SENSORS_COUNT = 4;
    private static final int SENSORS_DATA_QUEUE_CAPACITY = 1000;

    public static void main(String[] args) throws InterruptedException {
        var sensorsDataChannel = new SensorsDataQueueChannel(SENSORS_DATA_QUEUE_CAPACITY);
        var sensorsDataServer = new SensorsDataServerImpl(sensorsDataChannel);
        var fakeSensorDataGenerator = new FakeSensorDataGenerator(sensorsDataServer, SENSORS_COUNT);
        var sensorDataProcessingFlow = new SensorDataProcessingFlowImpl(sensorsDataChannel);

        sensorDataProcessingFlow.bindProcessor(ALL_ROOMS_BINDING, new SensorDataProcessorCommon());
        sensorDataProcessingFlow.bindProcessor(ALL_ROOMS_BINDING, new SensorDataProcessorErrors());
        sensorDataProcessingFlow.bindProcessor(ROOM_NAME_BINDING, new SensorDataProcessorRoom(ROOM_NAME_BINDING));
        sensorDataProcessingFlow.bindProcessor(ALL_ROOMS_BINDING, new SensorDataProcessorBuffered(BUFFER_SIZE, new SensorDataBufferedWriterFake()));
        fakeSensorDataGenerator.start();
        sensorDataProcessingFlow.startProcessing();

        TimeUnit.SECONDS.sleep(10);

        fakeSensorDataGenerator.stop();
        sensorDataProcessingFlow.stopProcessing();
    }
}