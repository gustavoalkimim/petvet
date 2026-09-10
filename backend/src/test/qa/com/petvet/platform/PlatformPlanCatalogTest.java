package com.petvet.platform;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlatformPlanCatalogTest {

    @Test
    void freePlanHasBasicLimitsAndNoMarketplace() {
        ProductPlanCatalog catalog = new ProductPlanCatalog();

        ProductPlan free = catalog.getPlan(ProductPlanTier.FREE);

        assertNotNull(free);
        assertEquals(3, free.getPetLimit());
        assertFalse(free.isMarketplaceEnabled());
        assertTrue(free.getFeatures().contains("Busca básica por pet shops"));
    }

    @Test
    void proPlanIncludesAdvancedMetricsAndMarketplace() {
        ProductPlanCatalog catalog = new ProductPlanCatalog();

        ProductPlan pro = catalog.getPlan(ProductPlanTier.PRO);

        assertNotNull(pro);
        assertEquals(1000, pro.getPetLimit());
        assertTrue(pro.isMarketplaceEnabled());
        assertTrue(pro.getMetrics().contains("Retenção e performance por unidade"));
        assertTrue(pro.getFeatures().contains("Dashboard de métricas avançadas"));
    }
}
