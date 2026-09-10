package com.petvet.platform;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlatformDashboardCatalogTest {

    @Test
    void freePetOwnerPlanShowsPersonalDashboard() {
        ProductDashboardCatalog catalog = new ProductDashboardCatalog();

        ProductDashboardConfig dashboard = catalog.forUser(
                new UserPlanProfile(UserRole.PET_OWNER, ProductPlanTier.FREE, 2, false)
        );

        assertNotNull(dashboard);
        assertEquals("Visão do tutor", dashboard.getTitle());
        assertTrue(dashboard.getMetrics().contains("Pets ativos"));
        assertFalse(dashboard.isMarketplaceEnabled());
    }

    @Test
    void clinicOwnerProPlanShowsOperationalDashboard() {
        ProductDashboardCatalog catalog = new ProductDashboardCatalog();

        ProductDashboardConfig dashboard = catalog.forUser(
                new UserPlanProfile(UserRole.CLINIC_OWNER, ProductPlanTier.PRO, 47, true)
        );

        assertNotNull(dashboard);
        assertEquals("Operação da clínica", dashboard.getTitle());
        assertTrue(dashboard.getMetrics().contains("Retenção por pet"));
        assertTrue(dashboard.isMarketplaceEnabled());
    }
}
