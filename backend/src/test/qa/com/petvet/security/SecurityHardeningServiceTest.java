package com.petvet.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SecurityHardeningServiceTest {

    @Test
    void loginAttemptServiceBlocksAfterRepeatedFailures() {
        LoginAttemptService service = new LoginAttemptService(Duration.ofMinutes(15), 5, Duration.ofMinutes(15));

        for (int i = 0; i < 4; i++) {
            service.recordFailure("user@example.com", "127.0.0.1");
        }

        assertFalse(service.isBlocked("user@example.com", "127.0.0.1"));

        service.recordFailure("user@example.com", "127.0.0.1");

        assertTrue(service.isBlocked("user@example.com", "127.0.0.1"));
    }

    private static final String TEST_SECRET = "test-secret-key-with-at-least-32-bytes";

    @Test
    void refreshTokenServiceGeneratesAndValidatesToken() {
        RefreshTokenService service = new RefreshTokenService(TEST_SECRET);
        String token = service.generateToken("user@example.com");

        assertNotNull(token);
        assertTrue(token.length() > 32);
        assertEquals("user@example.com", service.extractUserEmail(token));
        assertTrue(service.isValid(token));

        String randomToken = UUID.randomUUID().toString();
        assertFalse(service.isValid(randomToken));
    }

    @Test
    void refreshTokenServiceRejectsRevokedTokens() {
        RefreshTokenService service = new RefreshTokenService(TEST_SECRET);
        String token = service.generateToken("user@example.com");

        service.revokeToken(token);

        assertFalse(service.isValid(token));
    }

    @Test
    void jwtServiceRejectsRevokedAccessTokens() {
        JwtService service = new JwtService(TEST_SECRET, 60000L);

        UserDetails user = new org.springframework.security.core.userdetails.User(
                "user@example.com",
                "secret",
                java.util.List.of()
        );

        String token = service.generateToken(user);
        service.revokeToken(token);

        assertFalse(service.isTokenValid(token, user));
    }

    @Test
    void servicesRefuseToStartWithoutSecret() {
        var accessError = assertThrows(IllegalStateException.class, () -> new JwtService("", 60000L));
        var refreshError = assertThrows(IllegalStateException.class, () -> new RefreshTokenService(null));

        assertTrue(accessError.getMessage().contains("JWT_SECRET"));
        assertTrue(refreshError.getMessage().contains("JWT_REFRESH_SECRET"));
    }

    @Test
    void servicesRefuseSecretsShorterThan32Bytes() {
        assertThrows(IllegalStateException.class, () -> new JwtService("curto-demais", 60000L));
        assertThrows(IllegalStateException.class, () -> new RefreshTokenService("petvet-jwt-signing-key-2025"));
    }
}
