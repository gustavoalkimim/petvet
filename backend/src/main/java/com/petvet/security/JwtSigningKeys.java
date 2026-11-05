package com.petvet.security;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Cria as chaves HMAC dos tokens a partir da configuração, sem nenhum valor padrão no código.
 * Segredo ausente ou curto derruba a aplicação na subida, em vez de ela rodar com uma chave conhecida.
 */
final class JwtSigningKeys {

    /** HMAC-SHA256 exige pelo menos 256 bits de chave. */
    static final int MIN_SECRET_BYTES = 32;

    private JwtSigningKeys() {
    }

    static SecretKey fromSecret(String secret, String environmentVariable) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(environmentVariable + " is not set. Configure it before starting the application.");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                environmentVariable + " must have at least " + MIN_SECRET_BYTES + " bytes (HMAC-SHA256).");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
