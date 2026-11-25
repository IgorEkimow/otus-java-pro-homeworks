package ru.otus.java.pro.homeworks.homework17.server;

import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import ru.otus.java.pro.homeworks.homework17.generated.NumbersServiceGrpc;
import ru.otus.java.pro.homeworks.homework17.generated.SequenceRequest;
import ru.otus.java.pro.homeworks.homework17.generated.SequenceResponse;

public class NumbersService extends NumbersServiceGrpc.NumbersServiceImplBase implements AutoCloseable {
    private final ScheduledThreadPoolExecutor scheduler;
    private final long intervalNanos;

    public NumbersService() {
        this(Duration.ofSeconds(2));
    }

    public NumbersService(Duration interval) {
        Objects.requireNonNull(interval, "interval");

        if (interval.isZero() || interval.isNegative()) {
            throw new IllegalArgumentException("Интервал должен быть больше нуля");
        }

        intervalNanos = interval.toNanos();
        scheduler = new ScheduledThreadPoolExecutor(2);
        scheduler.setRemoveOnCancelPolicy(true);
    }

    @Override
    public void generateSequence(SequenceRequest request, StreamObserver<SequenceResponse> responseObserver) {
        if (request.getFirstValue() > request.getLastValue()) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("firstValue не должен быть больше lastValue").asRuntimeException());

            return;
        }

        if (request.getFirstValue() == request.getLastValue()) {
            responseObserver.onCompleted();

            return;
        }

        var observer = (ServerCallStreamObserver<SequenceResponse>) responseObserver;
        var sequence = new Sequence(request, observer);
        observer.setOnCancelHandler(sequence::cancel);

        try {
            sequence.start();
        } catch (RejectedExecutionException e) {
            responseObserver.onError(Status.UNAVAILABLE.withDescription("Сервер завершает работу").asRuntimeException());
        }
    }

    @Override
    public void close() {
        scheduler.shutdownNow();
    }

    private final class Sequence implements Runnable {
        private final ServerCallStreamObserver<SequenceResponse> observer;
        private final int lastValue;
        private long nextValue;
        private volatile ScheduledFuture<?> future;
        private volatile boolean cancelled;

        private Sequence(SequenceRequest request, ServerCallStreamObserver<SequenceResponse> observer) {
            this.observer = observer;
            lastValue = request.getLastValue();
            nextValue = (long) request.getFirstValue() + 1;
        }

        private void start() {
            future = scheduler.scheduleWithFixedDelay(this, intervalNanos, intervalNanos, TimeUnit.NANOSECONDS);
            if (cancelled || observer.isCancelled()) {
                cancel();
            }
        }

        @Override
        public void run() {
            if (cancelled || observer.isCancelled()) {
                cancel();

                return;
            }

            if (!observer.isReady()) {
                return;
            }

            try {
                observer.onNext(SequenceResponse.newBuilder().setValue((int) nextValue).build());
                if (nextValue == lastValue) {
                    cancel();
                    observer.onCompleted();
                } else {
                    nextValue++;
                }
            } catch (RuntimeException e) {
                cancel();
                if (!observer.isCancelled()) {
                    observer.onError(Status.INTERNAL.withDescription("Не удалось передать следующее число").withCause(e).asRuntimeException());
                }
            }
        }

        private void cancel() {
            cancelled = true;
            var task = future;
            if (task != null) {
                task.cancel(false);
            }
        }
    }
}