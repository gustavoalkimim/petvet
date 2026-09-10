package com.petvet.security;

import org.junit.jupiter.api.Test;

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

    @Test
    void refreshTokenServiceGeneratesAndValidatesToken() {
        RefreshTokenService service = new RefreshTokenService();
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
        RefreshTokenService service = new RefreshTokenService();
        String token = service.generateToken("user@example.com");

        service.revokeToken(token);

        assertFalse(service.isValid(token));
    }

    @Test
    void jwtServiceRejectsRevokedAccessTokens() {
        JwtService service = new JwtService();
        service.setJwtSecretForTest("test-secret-key-1234567890");
        service.setJwtExpirationMsForTest(60000L);

        UserDetails user = new org.springframework.security.core.userdetails.User(
                "user@example.com",
                "secret",
                java.util.List.of()
        );

        String token = service.generateToken(user);
        service.revokeToken(token);

        assertFalse(service.isTokenValid(token, user));
    }
}
