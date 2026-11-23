package ru.otus.java.pro.homeworks.homework12.web;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class SessionManager {
    static final Duration LIFETIME = Duration.ofMinutes(30);
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final Clock clock;

    SessionManager() {
        this(Clock.systemUTC());
    }

    SessionManager(Clock clock) {
        this.clock = clock;
    }

    Session create() {
        Instant now = clock.instant();
        sessions.values().removeIf(session -> !session.expiresAt().isAfter(now));
        Session session = new Session(token(), token(), now.plus(LIFETIME));
        sessions.put(session.id(), session);

        return session;
    }

    Session find(String id) {
        if (id == null) {
            return null;
        }

        Session session = sessions.get(id);

        if (session != null && !session.expiresAt().isAfter(clock.instant())) {
            sessions.remove(id, session);
            return null;
        }

        return session;
    }

    void remove(String id) {
        if (id != null) {
            sessions.remove(id);
        }
    }

    private String token() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    record Session(String id, String csrfToken, Instant expiresAt) {}
}