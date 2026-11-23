package ru.otus.java.pro.homeworks.homework11;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

public final class MemoryPressureDemo {
    private static final int ENTRY_COUNT = 128;
    private static final int VALUE_SIZE = 128 * 1024;
    private static final int PRESSURE_BLOCK_SIZE = 512 * 1024;
    private static final List<GarbageCollectorMXBean> COLLECTORS = ManagementFactory.getGarbageCollectorMXBeans();

    private MemoryPressureDemo() {}

    public static void main(String[] args) {
        if (Runtime.getRuntime().maxMemory() > 80L * 1024 * 1024) {
            throw new IllegalStateException("Запустите демонстрацию с -Xmx64m");
        }

        HwCache<String, byte[]> cache = new MyCache<>();
        String retainedKey = new String("retained-key");
        cache.put(retainedKey, new byte[VALUE_SIZE]);
        populate(cache);

        long collectionsBefore = collectionCount();
        int remaining = applyMemoryPressure(cache);
        long collections = collectionCount() - collectionsBefore;
        boolean retained = cache.get(retainedKey) != null;
        Reference.reachabilityFence(retainedKey);

        System.out.printf("CLEARED=%d/%d, RETAINED=%s, GC_RUNS=%d%n", ENTRY_COUNT - remaining, ENTRY_COUNT, retained, collections);
        if (remaining != 0 || !retained || collections == 0) {
            throw new IllegalStateException("Проверка очистки кэша при нехватке памяти не пройдена");
        }
    }

    private static void populate(HwCache<String, byte[]> cache) {
        List<String> keys = new ArrayList<>(ENTRY_COUNT);

        for (int index = 0; index < ENTRY_COUNT; index++) {
            String key = key(index);
            keys.add(key);
            cache.put(key, new byte[VALUE_SIZE]);
        }

        int present = countPresent(cache);
        Reference.reachabilityFence(keys);

        if (present != ENTRY_COUNT) {
            throw new IllegalStateException("Не все значения попали в кэш");
        }

        System.out.printf("BEFORE=%d, PAYLOAD_MIB=%d%n", present, ENTRY_COUNT * VALUE_SIZE / (1024 * 1024));
    }

    private static int applyMemoryPressure(HwCache<String, byte[]> cache) {
        byte[][] pressure = new byte[32][];
        int remaining = ENTRY_COUNT;

        for (int block = 0; block < 512 && remaining > 0; block++) {
            pressure[block % pressure.length] = new byte[PRESSURE_BLOCK_SIZE];
            if (block % 8 == 7) {
                remaining = countPresent(cache);
            }
        }

        Reference.reachabilityFence(pressure);

        return remaining;
    }

    private static int countPresent(HwCache<String, byte[]> cache) {
        int present = 0;
        for (int index = 0; index < ENTRY_COUNT; index++) {
            if (cache.get(key(index)) != null) {
                present++;
            }
        }

        return present;
    }

    private static String key(int index) {
        return "entry-" + index;
    }

    private static long collectionCount() {
        long count = 0;
        for (GarbageCollectorMXBean collector : COLLECTORS) {
            count += Math.max(0, collector.getCollectionCount());
        }

        return count;
    }
}