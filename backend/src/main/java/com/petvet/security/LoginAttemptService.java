package com.petvet.security;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final Duration window;
    private final int maxAttempts;
    private final Duration lockDuration;
    private final Map<String, AttemptState> attempts = new ConcurrentHashMap<>();

    public LoginAttemptService() {
        this(Duration.ofMinutes(15), 5, Duration.ofMinutes(15));
    }

    public LoginAttemptService(Duration window, int maxAttempts, Duration lockDuration) {
        this.window = window;
        this.maxAttempts = maxAttempts;
        this.lockDuration = lockDuration;
    }

    public void recordFailure(String email, String ipAddress) {
        AttemptState state = attempts.computeIfAbsent(normalizeKey(email, ipAddress), ignored -> new AttemptState());
        state.failedAttempts++;
        state.lastAttemptAt = Instant.now();
        if (state.failedAttempts >= maxAttempts) {
            state.lockedUntil = Instant.now().plus(lockDuration);
        }
    }

    public void recordSuccess(String email, String ipAddress) {
        attempts.remove(normalizeKey(email, ipAddress));
    }

    public boolean isBlocked(String email, String ipAddress) {
        AttemptState state = attempts.get(normalizeKey(email, ipAddress));
        if (state == null) {
            return false;
        }

        if (state.lockedUntil != null && state.lockedUntil.isAfter(Instant.now())) {
            return true;
        }

        if (state.lockedUntil != null && state.lockedUntil.isBefore(Instant.now())) {
            attempts.remove(normalizeKey(email, ipAddress));
            return false;
        }

        return state.failedAttempts >= maxAttempts;
    }

    private String normalizeKey(String email, String ipAddress) {
        String normalizedEmail = email == null ? "unknown" : email.trim().toLowerCase();
        String normalizedIp = ipAddress == null ? "unknown" : ipAddress.trim();
        return normalizedEmail + ":" + normalizedIp;
    }

    private static final class AttemptState {
        private int failedAttempts;
        private Instant lastAttemptAt;
        private Instant lockedUntil;
    }
}
