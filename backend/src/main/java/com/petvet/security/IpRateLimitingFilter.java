package com.petvet.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class IpRateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    private static final long WINDOW_MS = 60_000L;

    private final Map<String, RequestBucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = resolveClientIp(request);
        RequestBucket bucket = buckets.computeIfAbsent(clientIp, key -> new RequestBucket());

        long now = System.currentTimeMillis();
        synchronized (bucket) {
            if (now - bucket.windowStartMs >= WINDOW_MS) {
                bucket.windowStartMs = now;
                bucket.count = 0;
            }

            if (bucket.count >= MAX_REQUESTS_PER_MINUTE) {
                response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"message\":\"Too many requests. Please retry later.\"}");
                return;
            }

            bucket.count++;
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private static final class RequestBucket {
        private long windowStartMs = System.currentTimeMillis();
        private int count = 0;
    }
}
