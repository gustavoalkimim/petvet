package com.petvet.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String DEFAULT_JWT_SECRET = "petvet-jwt-signing-key-2025";
    private static final long DEFAULT_JWT_EXPIRATION_MS = 3_600_000L;
    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();

    @Value("${app.jwt.secret:petvet-jwt-signing-key-2025}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    public String extractUsername(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (token == null || token.isBlank() || revokedTokens.contains(token)) {
            return false;
        }

        if (userDetails == null) {
            return false;
        }

        final String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public void revokeToken(String token) {
        if (token != null && !token.isBlank()) {
            revokedTokens.add(token);
        }
    }

    public void setJwtSecretForTest(String secret) {
        this.jwtSecret = secret;
    }

    public void setJwtExpirationMsForTest(long expirationMs) {
        this.jwtExpirationMs = expirationMs;
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception ex) {
            return true;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claimsResolver.apply(claims);
        } catch (Exception ex) {
            return null;
        }
    }

    private SecretKey getSigningKey() {
        String activeSecret = jwtSecret != null && !jwtSecret.isBlank() ? jwtSecret : DEFAULT_JWT_SECRET;
        byte[] keyBytes = activeSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
