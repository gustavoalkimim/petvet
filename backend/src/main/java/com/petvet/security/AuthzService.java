package com.petvet.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthzService {

    public boolean canAccessClinic(Authentication authentication, Long clinicId) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return false;
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            return false;
        }

        return principal.getClinicId() != null && principal.getClinicId().equals(clinicId);
    }

    public boolean canAccessPet(Authentication authentication, Long clinicId, Long petClinicId) {
        return canAccessClinic(authentication, clinicId) && (petClinicId == null || petClinicId.equals(clinicId));
    }
}
