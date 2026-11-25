package ru.otus.java.pro.homeworks.homework17.server;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NumbersServer {
    private static final Logger log = LoggerFactory.getLogger(NumbersServer.class);
    private static final String HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8190;

    private NumbersServer() {}

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = args.length == 0 ? DEFAULT_PORT : Integer.parseInt(args[0]);
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Порт должен быть в диапазоне от 1 до 65535");
        }

        try (var service = new NumbersService()) {
            var server = NettyServerBuilder.forAddress(new InetSocketAddress(HOST, port)).addService(service).build();
            var shutdownHook = new Thread(() -> {
                server.shutdownNow();
                service.close();
            }, "numbers-server-shutdown");

            Runtime.getRuntime().addShutdownHook(shutdownHook);

            try {
                server.start();
                log.info("Сервер чисел запущен: {}:{}", HOST, server.getPort());
                server.awaitTermination();
            } finally {
                stop(server);

                try {
                    Runtime.getRuntime().removeShutdownHook(shutdownHook);
                } catch (IllegalStateException ignored) {}
            }
        }
    }

    private static void stop(Server server) {
        server.shutdownNow();
        try {
            if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Истекло время ожидания остановки gRPC-сервера");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}