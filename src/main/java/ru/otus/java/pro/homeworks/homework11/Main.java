package ru.otus.java.pro.homeworks.homework11;

import ru.otus.java.pro.homeworks.homework11.model.Address;
import ru.otus.java.pro.homeworks.homework11.model.Client;
import ru.otus.java.pro.homeworks.homework11.model.Phone;
import ru.otus.java.pro.homeworks.homework11.service.DbService;
import ru.otus.java.pro.homeworks.homework11.util.HibernateUtils;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Main {
    private static final int CLIENTS = 100;
    private static final int READS = 2_000;
    private static final int ROUNDS = 5;
    private static volatile long checksum;

    public static void main(String[] args) {
        System.setProperty("org.jboss.logging.provider", "slf4j");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");

        try (var sessionFactory = HibernateUtils.createSessionFactory("cache_demo")) {
            var cache = new MyCache<String, Client>();
            var database = new DbService(sessionFactory);
            var cached = new DbService(sessionFactory, cache);
            var ids = new ArrayList<Long>();

            for (int i = 0; i < CLIENTS; i++) {
                var client = new Client("Client " + i);
                client.setAddress(new Address("Street " + i));
                client.addPhone(new Phone("+7-900-" + i));
                ids.add(database.saveClient(client));
            }

            var keys = warmCache(cached, cache, ids);
            measure(database, ids);
            measure(cached, ids);

            var statistics = sessionFactory.getStatistics();
            long[][] times = new long[2][ROUNDS];
            long[] sqlCounts = new long[2];
            DbService[] services = {database, cached};
            for (int round = 0; round < ROUNDS; round++) {
                for (int step = 0; step < services.length; step++) {
                    int index = (round + step) % services.length;
                    statistics.clear();
                    times[index][round] = measure(services[index], ids);
                    sqlCounts[index] += statistics.getPrepareStatementCount();
                }
            }

            Reference.reachabilityFence(keys);

            if (sqlCounts[1] != 0 || sqlCounts[0] < (long) READS * ROUNDS) {
                throw new IllegalStateException("Некорректное сравнение: проверьте обращения к БД");
            }

            double databaseNs = median(times[0]) / (double) READS;
            double cacheNs = median(times[1]) / (double) READS;
            System.out.printf(Locale.ROOT, "Медиана %d серий по %d чтений:%n", ROUNDS, READS);
            System.out.printf(Locale.ROOT, "Без кэша: %.3f мкс/чтение, SQL: %d%n", databaseNs / 1_000, sqlCounts[0]);
            System.out.printf(Locale.ROOT, "С кэшем:  %.3f мкс/чтение, SQL: %d%n", cacheNs / 1_000, sqlCounts[1]);
            System.out.printf(Locale.ROOT, "Ускорение: %.1fx (контрольная сумма: %d)%n", databaseNs / cacheNs, checksum);
        }
    }

    private static List<String> warmCache(DbService service, HwCache<String, Client> cache, List<Long> ids) {
        var keys = new ArrayList<String>();
        HwListener<String, Client> listener = (key, value, action) -> {
            if ("put".equals(action)) {
                keys.add(key);
            }
        };

        cache.addListener(listener);

        try {
            for (Long id : ids) {
                service.getClient(id).orElseThrow();
            }
        } finally {
            cache.removeListener(listener);
        }

        return keys;
    }

    private static long measure(DbService service, List<Long> ids) {
        long sum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < READS; i++) {
            Client client = service.getClient(ids.get(i % ids.size())).orElseThrow();
            sum += client.getId() + client.getName().length() + client.getPhones().size();
        }

        long elapsed = System.nanoTime() - start;
        checksum = sum;

        return elapsed;
    }

    private static long median(long[] times) {
        long[] sorted = times.clone();
        Arrays.sort(sorted);

        return sorted[sorted.length / 2];
    }
}