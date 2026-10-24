package ru.otus.java.pro.homeworks.homework8.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class JsonFileService {
    private final ObjectMapper objectMapper;

    public JsonFileService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public <T> T read(File file, Class<T> clazz) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("Файл не найден: " + file);
        }

        if (!file.canRead()) {
            throw new IOException("Нет прав на чтение: " + file);
        }

        return objectMapper.readValue(file, clazz);
    }

    public void write(File file, Object object) throws IOException {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        objectMapper.writeValue(file, object);
    }
}