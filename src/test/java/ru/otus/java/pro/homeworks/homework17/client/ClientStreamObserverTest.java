package ru.otus.java.pro.homeworks.homework17.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import ru.otus.java.pro.homeworks.homework17.generated.SequenceResponse;

class ClientStreamObserverTest {
    @Test
    void returnsZeroBeforeReceivingAnyNumber() {
        var observer = new ClientStreamObserver();
        assertEquals(0, observer.consumeLatestValue());
        assertNull(observer.getError());
    }

    @Test
    void consumesReceivedNumberOnlyOnce() {
        var observer = new ClientStreamObserver();
        observer.onNext(response(7));
        assertEquals(7, observer.consumeLatestValue());
        assertEquals(0, observer.consumeLatestValue());
    }

    @Test
    void keepsOnlyTheLatestOfSeveralArrivals() {
        var observer = new ClientStreamObserver();
        observer.onNext(response(2));
        observer.onNext(response(3));
        observer.onNext(response(4));
        assertEquals(4, observer.consumeLatestValue());
        assertEquals(0, observer.consumeLatestValue());
    }

    @Test
    void producesTheSequenceFromTheAssignment() {
        var observer = new ClientStreamObserver();
        long currentValue = 0;
        currentValue += observer.consumeLatestValue() + 1;
        assertEquals(1, currentValue);
        observer.onNext(response(2));
        currentValue += observer.consumeLatestValue() + 1;
        assertEquals(4, currentValue);
        currentValue += observer.consumeLatestValue() + 1;
        assertEquals(5, currentValue);
        observer.onNext(response(3));
        currentValue += observer.consumeLatestValue() + 1;
        assertEquals(9, currentValue);
        currentValue += observer.consumeLatestValue() + 1;
        assertEquals(10, currentValue);
        observer.onNext(response(4));
        currentValue += observer.consumeLatestValue() + 1;
        assertEquals(15, currentValue);
        currentValue += observer.consumeLatestValue() + 1;
        assertEquals(16, currentValue);
    }

    @Test
    void completionKeepsTheLastNumberAvailableForTheClientLoop() {
        var observer = new ClientStreamObserver();
        observer.onNext(response(30));
        observer.onCompleted();
        assertEquals(30, observer.consumeLatestValue());
        assertEquals(0, observer.consumeLatestValue());
        assertNull(observer.getError());
    }

    @Test
    void exposesStreamFailureToTheClient() {
        var observer = new ClientStreamObserver();
        var failure = new IllegalStateException("Server disconnected");
        observer.onError(failure);
        assertSame(failure, observer.getError());
    }

    private static SequenceResponse response(int value) {
        return SequenceResponse.newBuilder().setValue(value).build();
    }
}