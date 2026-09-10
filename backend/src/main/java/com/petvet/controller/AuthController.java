package com.petvet.controller;

import com.petvet.model.User;
import com.petvet.repository.UserRepository;
import com.petvet.security.JwtService;
import com.petvet.security.LoginAttemptService;
import com.petvet.security.RefreshTokenService;
import com.petvet.security.SecurityAuditService;
import com.petvet.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final LoginAttemptService loginAttemptService;
    private final SecurityAuditService securityAuditService;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         JwtService jwtService,
                         RefreshTokenService refreshTokenService,
                         LoginAttemptService loginAttemptService,
                         SecurityAuditService securityAuditService,
                         CustomUserDetailsService customUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.loginAttemptService = loginAttemptService;
        this.securityAuditService = securityAuditService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        String normalizedEmail = request.email.trim().toLowerCase();

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            return ResponseEntity.badRequest().body(new AuthResponse(null, null, "Email already registered."));
        }

        if (!isStrongPassword(request.password)) {
            return ResponseEntity.badRequest().body(
                new AuthResponse(null, null,
                    "Password must contain at least 12 characters, including uppercase, lowercase, number and symbol.")
            );
        }

        User user = new User();
        user.setName(request.name.trim());
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password));
        user.setRole("USER");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(normalizedEmail);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = refreshTokenService.generateToken(normalizedEmail);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Registration successful."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        String normalizedEmail = request.email.trim().toLowerCase();
        String clientIp = extractClientIpAddress(httpServletRequest);

        if (loginAttemptService.isBlocked(normalizedEmail, clientIp)) {
            securityAuditService.logSecurityEvent("AUTH_BLOCKED", normalizedEmail, clientIp, "Repeated failed login attempts");
            return ResponseEntity.status(429).body(new AuthResponse(null, null, "Too many failed login attempts. Please try again later."));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedEmail, request.password)
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(normalizedEmail);
                String accessToken = jwtService.generateToken(userDetails);
                String refreshToken = refreshTokenService.generateToken(normalizedEmail);
                loginAttemptService.recordSuccess(normalizedEmail, clientIp);
                securityAuditService.logSecurityEvent("AUTH_SUCCESS", normalizedEmail, clientIp, "User authenticated successfully");
                return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Login successful."));
            }
        } catch (Exception ex) {
            loginAttemptService.recordFailure(normalizedEmail, clientIp);
            securityAuditService.logSecurityEvent("AUTH_FAILURE", normalizedEmail, clientIp, ex.getMessage());
            return ResponseEntity.status(401).body(new AuthResponse(null, null, "Invalid credentials."));
        }

        return ResponseEntity.status(401).body(new AuthResponse(null, null, "Invalid credentials."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        if (request.refreshToken == null || !refreshTokenService.isValid(request.refreshToken)) {
            return ResponseEntity.status(401).body(new AuthResponse(null, null, "Invalid refresh token."));
        }

        String email = refreshTokenService.extractUserEmail(request.refreshToken);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = refreshTokenService.generateToken(email);
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Token refreshed."));
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@RequestBody(required = false) LogoutRequest request,
                                             HttpServletRequest httpServletRequest) {
        String email = request != null && request.email != null ? request.email.trim().toLowerCase() : "unknown";
        String clientIp = extractClientIpAddress(httpServletRequest);

        String accessToken = null;
        String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            accessToken = authorization.substring(7);
            jwtService.revokeToken(accessToken);
        }

        if (request != null && request.refreshToken != null && !request.refreshToken.isBlank()) {
            refreshTokenService.revokeToken(request.refreshToken);
        }

        loginAttemptService.recordSuccess(email, clientIp);
        securityAuditService.logSecurityEvent("AUTH_LOGOUT", email, clientIp,
                accessToken != null ? "User logged out and access token was revoked" : "User logged out");
        return ResponseEntity.ok(new AuthResponse(null, null, "Logged out successfully."));
    }

    private String extractClientIpAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < 12) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSymbol = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));

        return hasUpper && hasLower && hasDigit && hasSymbol;
    }

    public static class RegisterRequest {
        @NotBlank
        public String name;

        @NotBlank
        @Email
        public String email;

        @NotBlank
        public String password;
    }

    public static class LoginRequest {
        @NotBlank
        @Email
        public String email;

        @NotBlank
        public String password;
    }

    public static class RefreshRequest {
        @NotBlank
        public String refreshToken;
    }

    public static class LogoutRequest {
        public String email;
        public String refreshToken;
    }

    public static class AuthResponse {
        public String token;
        public String refreshToken;
        public String message;

        public AuthResponse() {
        }

        public AuthResponse(String token, String refreshToken, String message) {
            this.token = token;
            this.refreshToken = refreshToken;
            this.message = message;
        }
    }
}
