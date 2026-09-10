package com.petvet.service;

import com.petvet.platform.ProductPlan;
import com.petvet.platform.ProductPlanCatalog;
import com.petvet.platform.ProductPlanTier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final ProductPlanCatalog catalog = new ProductPlanCatalog();

    public ProductPlan getPlan(ProductPlanTier tier) {
        return catalog.getPlan(tier);
    }

    public List<ProductPlan> getAllPlans() {
        return catalog.getAllPlans();
    }
}
