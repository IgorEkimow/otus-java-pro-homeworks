package ru.otus.java.pro.homeworks.homework11;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(MyCache.class);
    private final Map<K, V> values = new WeakHashMap<>();
    private final CopyOnWriteArrayList<HwListener<K, V>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");

        synchronized (values) {
            values.put(key, value);
        }

        notifyListeners(key, value, "put");
    }

    @Override
    public void remove(K key) {
        Objects.requireNonNull(key, "key");
        V value;

        synchronized (values) {
            value = values.remove(key);
        }

        notifyListeners(key, value, "remove");
    }

    @Override
    public V get(K key) {
        Objects.requireNonNull(key, "key");
        V value;

        synchronized (values) {
            value = values.get(key);
        }

        notifyListeners(key, value, "get");

        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.addIfAbsent(Objects.requireNonNull(listener, "listener"));
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(Objects.requireNonNull(listener, "listener"));
    }

    private void notifyListeners(K key, V value, String action) {
        for (HwListener<K, V> listener : listeners) {
            try {
                listener.notify(key, value, action);
            } catch (RuntimeException exception) {
                logger.warn("Cache listener failed on {}", action, exception);
            }
        }
    }
}