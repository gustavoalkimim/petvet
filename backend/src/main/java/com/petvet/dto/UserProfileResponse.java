package com.petvet.dto;

import com.petvet.platform.UserRole;
import com.petvet.platform.ProductPlanTier;

public class UserProfileResponse {
    private UserRole role;
    private ProductPlanTier planTier;
    private int petCount;
    private boolean marketplaceAccess;
    private boolean canAddPet;

    public UserProfileResponse(UserRole role, ProductPlanTier planTier, int petCount,
                               boolean marketplaceAccess, boolean canAddPet) {
        this.role = role;
        this.planTier = planTier;
        this.petCount = petCount;
        this.marketplaceAccess = marketplaceAccess;
        this.canAddPet = canAddPet;
    }

    public UserRole getRole() { return role; }
    public ProductPlanTier getPlanTier() { return planTier; }
    public int getPetCount() { return petCount; }
    public boolean isMarketplaceAccess() { return marketplaceAccess; }
    public boolean isCanAddPet() { return canAddPet; }
}
