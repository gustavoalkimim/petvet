package com.petvet.platform;

import java.util.Objects;

public class UserPlanProfile {

    private final UserRole role;
    private final ProductPlanTier planTier;
    private final int petCount;
    private final boolean marketplaceAccess;

    public UserPlanProfile(UserRole role, ProductPlanTier planTier, int petCount, boolean marketplaceAccess) {
        this.role = role;
        this.planTier = planTier;
        this.petCount = petCount;
        this.marketplaceAccess = marketplaceAccess;
    }

    public UserRole getRole() {
        return role;
    }

    public ProductPlanTier getPlanTier() {
        return planTier;
    }

    public int getPetCount() {
        return petCount;
    }

    public boolean hasMarketplaceAccess() {
        return marketplaceAccess;
    }

    public boolean canAddPet() {
        ProductPlanCatalog catalog = new ProductPlanCatalog();
        ProductPlan plan = catalog.getPlan(planTier);
        return petCount < plan.getPetLimit();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPlanProfile that = (UserPlanProfile) o;
        return petCount == that.petCount && marketplaceAccess == that.marketplaceAccess && role == that.role && planTier == that.planTier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, planTier, petCount, marketplaceAccess);
    }
}
