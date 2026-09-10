package com.petvet.controller;

import com.petvet.dto.DashboardResponse;
import com.petvet.dto.UserProfileResponse;
import com.petvet.platform.ProductDashboardConfig;
import com.petvet.platform.ProductPlanTier;
import com.petvet.platform.UserPlanProfile;
import com.petvet.platform.UserRole;
import com.petvet.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(403).build();
        }

        UserPlanProfile profile = new UserPlanProfile(
                UserRole.PET_OWNER,
                ProductPlanTier.FREE,
                2,
                false
        );

        UserProfileResponse response = new UserProfileResponse(
                profile.getRole(),
                profile.getPlanTier(),
                profile.getPetCount(),
                profile.hasMarketplaceAccess(),
                profile.canAddPet()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getUserDashboard(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(403).build();
        }

        UserPlanProfile profile = new UserPlanProfile(
                UserRole.PET_OWNER,
                ProductPlanTier.FREE,
                2,
                false
        );

        ProductDashboardConfig dashboard = userProfileService.getDashboard(profile);

        DashboardResponse response = new DashboardResponse(
                dashboard.getTitle(),
                dashboard.getMetrics(),
                dashboard.getHighlights(),
                dashboard.isMarketplaceEnabled()
        );

        return ResponseEntity.ok(response);
    }
}
