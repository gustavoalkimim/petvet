package com.petvet.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigurationTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    private static CorsConfiguration configFor(CorsConfigurationSource source) {
        return source.getCorsConfiguration(new MockHttpServletRequest("GET", "/api/plans"));
    }

    @Test
    void allowsOnlyConfiguredOriginsTrimmed() {
        CorsConfiguration config = configFor(securityConfig.corsConfigurationSource(
            List.of(" https://app.petvet.com.br ", "https://admin.petvet.com.br", "")));

        assertEquals(List.of("https://app.petvet.com.br", "https://admin.petvet.com.br"), config.getAllowedOrigins());
        assertEquals("https://app.petvet.com.br", config.checkOrigin("https://app.petvet.com.br"));
        assertNull(config.checkOrigin("https://site-malicioso.com"));
        assertTrue(config.getAllowCredentials());
    }

    @Test
    void refusesWildcardBecauseCredentialsAreAllowed() {
        assertThrows(IllegalStateException.class,
            () -> securityConfig.corsConfigurationSource(List.of("https://app.petvet.com.br", "*")));
    }

    @Test
    void refusesEmptyOriginList() {
        assertThrows(IllegalStateException.class, () -> securityConfig.corsConfigurationSource(List.of(" ")));
    }
}
