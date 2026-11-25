package ru.otus.java.pro.homeworks.homework17.client;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.java.pro.homeworks.homework17.generated.SequenceResponse;

public class ClientStreamObserver implements StreamObserver<SequenceResponse> {
    private static final Logger log = LoggerFactory.getLogger(ClientStreamObserver.class);
    private final AtomicInteger latestValue = new AtomicInteger();
    private volatile Throwable error;

    @Override
    public void onNext(SequenceResponse response) {
        latestValue.set(response.getValue());
        log.info("new value:{}", response.getValue());
    }

    public int consumeLatestValue() {
        return latestValue.getAndSet(0);
    }

    public Throwable getError() {
        return error;
    }

    @Override
    public void onError(Throwable throwable) {
        error = throwable;

        if (Status.fromThrowable(throwable).getCode() == Status.Code.CANCELLED) {
            log.info("request cancelled");
        } else {
            log.error("request failed", throwable);
        }
    }

    @Override
    public void onCompleted() {
        log.info("request completed");
    }
}