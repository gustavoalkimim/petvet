package com.petvet.security;

import com.petvet.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthzServiceTest {

    private final AuthzService authzService = new AuthzService();

    @Test
    void adminCanAccessAnyClinic() {
        CustomUserDetails principal = new CustomUserDetails(
                "admin@petvet.com",
                "secret",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
                99L
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        assertTrue(authzService.canAccessClinic(authentication, 4L));
    }

    @Test
    void clinicUserCanAccessOnlyOwnClinic() {
        User user = new User();
        user.setId(1L);
        user.setRole("CLINIC");
        user.setClinic(null);

        CustomUserDetails principal = new CustomUserDetails(
                "ops@petvet.com",
                "secret",
                List.of(new SimpleGrantedAuthority("ROLE_CLINIC")),
                7L
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        assertTrue(authzService.canAccessClinic(authentication, 7L));
        assertFalse(authzService.canAccessClinic(authentication, 8L));
    }
}
