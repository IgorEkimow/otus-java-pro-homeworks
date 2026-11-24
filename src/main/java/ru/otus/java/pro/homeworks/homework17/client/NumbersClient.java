package ru.otus.java.pro.homeworks.homework17.client;

import io.grpc.Context;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.pro.homeworks.homework17.generated.NumbersServiceGrpc;
import ru.otus.java.pro.homeworks.homework17.generated.SequenceRequest;

public class NumbersClient {
    private static final Logger log = LoggerFactory.getLogger(NumbersClient.class);
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 8190;
    private static final int LAST_ITERATION = 50;

    public static void main(String[] args) throws InterruptedException {
        if (args.length > 2) {
            throw new IllegalArgumentException("Использование: NumbersClient [host] [port]");
        }

        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Порт должен быть в диапазоне от 1 до 65535");
        }

        ManagedChannel channel = NettyChannelBuilder.forAddress(host, port).usePlaintext().build();
        Context.CancellableContext requestContext = Context.current().withCancellation();

        try {
            log.info("numbers Client is starting, server={}:{}", host, port);
            var observer = new ClientStreamObserver();
            var request = SequenceRequest.newBuilder().setFirstValue(0).setLastValue(30).build();
            requestContext.run(() -> NumbersServiceGrpc.newStub(channel).generateSequence(request, observer));
            long currentValue = 0;

            for (int i = 0; i <= LAST_ITERATION; i++) {
                Throwable error = observer.getError();
                if (error != null) {
                    throw new IllegalStateException("Не удалось получить последовательность от сервера", error);
                }

                currentValue += observer.consumeLatestValue() + 1L;
                log.info("currentValue:{}", currentValue);

                if (i < LAST_ITERATION) {
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        } finally {
            requestContext.cancel(null);
            channel.shutdown();

            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("Канал не завершился вовремя; закрываем принудительно");
                }
            } finally {
                if (!channel.isTerminated()) {
                    channel.shutdownNow();
                }
            }
        }
    }
}