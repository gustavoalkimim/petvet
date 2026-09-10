package com.petvet.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class RefreshTokenService {

    private static final Duration TOKEN_TTL = Duration.ofDays(7);
    private final SecretKey signingKey;
    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();

    public RefreshTokenService(@Value("${app.jwt.refresh-secret:petvet-refresh-token-signing-key-2025}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public RefreshTokenService() {
        this("petvet-refresh-token-signing-key-2025");
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + TOKEN_TTL.toMillis()))
                .signWith(signingKey)
                .compact();
    }

    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isValid(String token) {
        if (token == null || token.isBlank() || revokedTokens.contains(token)) {
            return false;
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration() != null && claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    public void revokeToken(String token) {
        if (token != null && !token.isBlank()) {
            revokedTokens.add(token);
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }
}
