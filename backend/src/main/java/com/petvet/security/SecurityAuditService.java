package com.petvet.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SecurityAuditService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditService.class);

    public void logSecurityEvent(String eventType, String user, String ipAddress, String details) {
        logger.warn("SECURITY_EVENT eventType={} user={} ipAddress={} details={}", eventType, user, ipAddress, details);
    }
}
