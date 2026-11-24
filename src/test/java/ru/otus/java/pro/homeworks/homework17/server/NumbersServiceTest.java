package ru.otus.java.pro.homeworks.homework17.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;
import io.grpc.stub.StreamObserver;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.java.pro.homeworks.homework17.generated.NumbersServiceGrpc;
import ru.otus.java.pro.homeworks.homework17.generated.SequenceRequest;
import ru.otus.java.pro.homeworks.homework17.generated.SequenceResponse;

class NumbersServiceTest {
    private NumbersService service;
    private Server server;
    private ManagedChannel channel;

    @BeforeEach
    void startServer() throws Exception {
        service = new NumbersService(Duration.ofMillis(10));
        var name = InProcessServerBuilder.generateName();
        server = InProcessServerBuilder.forName(name).directExecutor().addService(service).build().start();
        channel = InProcessChannelBuilder.forName(name).directExecutor().build();
    }

    @AfterEach
    void stopServer() throws InterruptedException {
        channel.shutdownNow();
        server.shutdownNow();
        service.close();
        channel.awaitTermination(5, TimeUnit.SECONDS);
        server.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    void streamsNumbersExcludingFirstAndIncludingLast() {
        assertEquals(List.of(5, 6, 7), readSequence(4, 7));
    }

    @Test
    void completesAnEmptyRangeWithoutSendingNumbers() {
        assertEquals(List.of(), readSequence(4, 4));
    }

    @Test
    void rejectsReversedBounds() {
        var error = assertThrows(StatusRuntimeException.class, () -> readSequence(7, 4));
        assertEquals(Status.Code.INVALID_ARGUMENT, error.getStatus().getCode());
    }

    @Test
    void completesAtTheMaximumIntegerWithoutOverflowing() {
        assertEquals(List.of(Integer.MAX_VALUE - 1, Integer.MAX_VALUE), readSequence(Integer.MAX_VALUE - 2, Integer.MAX_VALUE));
    }

    @Test
    void supportsRangesContainingNegativeNumbersAndZero() {
        assertEquals(List.of(-2, -1, 0, 1), readSequence(-3, 1));
    }

    @Test
    void keepsConcurrentRequestsIndependent() throws Exception {
        var first = new CollectingObserver();
        var second = new CollectingObserver();
        var stub = NumbersServiceGrpc.newStub(channel).withDeadlineAfter(5, TimeUnit.SECONDS);
        stub.generateSequence(request(0, 4), first);
        stub.generateSequence(request(10, 13), second);
        assertEquals(List.of(1, 2, 3, 4), first.completed.get(5, TimeUnit.SECONDS));
        assertEquals(List.of(11, 12, 13), second.completed.get(5, TimeUnit.SECONDS));
    }

    @Test
    void allowsTheClientToCancelALongStream() throws Exception {
        var firstValue = new CompletableFuture<Integer>();
        var terminated = new CompletableFuture<Status.Code>();
        var observer = new ClientResponseObserver<SequenceRequest, SequenceResponse>() {
            private ClientCallStreamObserver<SequenceRequest> call;

            @Override
            public void beforeStart(ClientCallStreamObserver<SequenceRequest> requestStream) {
                call = requestStream;
            }

            @Override
            public void onNext(SequenceResponse value) {
                firstValue.complete(value.getValue());
                call.cancel("Client loop finished", null);
            }

            @Override
            public void onError(Throwable error) {
                terminated.complete(Status.fromThrowable(error).getCode());
            }

            @Override
            public void onCompleted() {
                terminated.complete(Status.Code.OK);
            }
        };

        NumbersServiceGrpc.newStub(channel).withDeadlineAfter(5, TimeUnit.SECONDS).generateSequence(request(0, Integer.MAX_VALUE), observer);

        assertEquals(1, firstValue.get(5, TimeUnit.SECONDS));
        assertEquals(Status.Code.CANCELLED, terminated.get(5, TimeUnit.SECONDS));
        assertEquals(List.of(21), readSequence(20, 21));
    }

    private List<Integer> readSequence(int firstValue, int lastValue) {
        var results = new ArrayList<Integer>();
        NumbersServiceGrpc.newBlockingStub(channel).withDeadlineAfter(5, TimeUnit.SECONDS).generateSequence(request(firstValue, lastValue)).forEachRemaining(response -> results.add(response.getValue()));

        return results;
    }

    private static SequenceRequest request(int firstValue, int lastValue) {
        return SequenceRequest.newBuilder().setFirstValue(firstValue).setLastValue(lastValue).build();
    }

    private static class CollectingObserver implements StreamObserver<SequenceResponse> {
        private final List<Integer> values = new ArrayList<>();
        private final CompletableFuture<List<Integer>> completed = new CompletableFuture<>();

        @Override
        public void onNext(SequenceResponse value) {
            values.add(value.getValue());
        }

        @Override
        public void onError(Throwable error) {
            completed.completeExceptionally(error);
        }

        @Override
        public void onCompleted() {
            completed.complete(List.copyOf(values));
        }
    }
}