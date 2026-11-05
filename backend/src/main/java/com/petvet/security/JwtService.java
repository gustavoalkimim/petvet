package com.petvet.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class JwtService {

    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet();
    private final SecretKey signingKey;
    private final long jwtExpirationMs;

    public JwtService(@Value("${app.jwt.secret:}") String jwtSecret,
                      @Value("${app.jwt.expiration-ms:3600000}") long jwtExpirationMs) {
        this.signingKey = JwtSigningKeys.fromSecret(jwtSecret, "JWT_SECRET");
        this.jwtExpirationMs = jwtExpirationMs;
    }

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
                .signWith(signingKey)
                .compact();
    }

    public void revokeToken(String token) {
        if (token != null && !token.isBlank()) {
            revokedTokens.add(token);
        }
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
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claimsResolver.apply(claims);
        } catch (Exception ex) {
            return null;
        }
    }
}
