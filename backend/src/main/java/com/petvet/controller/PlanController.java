package com.petvet.controller;

import com.petvet.dto.PlanResponse;
import com.petvet.platform.ProductPlan;
import com.petvet.platform.ProductPlanTier;
import com.petvet.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<PlanResponse>> getAllPlans() {
        List<ProductPlan> plans = planService.getAllPlans();
        List<PlanResponse> responses = plans.stream()
                .map(p -> new PlanResponse(
                        p.getTier(),
                        p.getLabel(),
                        p.getDescription(),
                        p.getPetLimit(),
                        p.isMarketplaceEnabled(),
                        p.getFeatures(),
                        p.getMetrics()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{tier}")
    public ResponseEntity<PlanResponse> getPlanByTier(@PathVariable String tier) {
        try {
            ProductPlanTier planTier = ProductPlanTier.valueOf(tier.toUpperCase());
            ProductPlan plan = planService.getPlan(planTier);
            PlanResponse response = new PlanResponse(
                    plan.getTier(),
                    plan.getLabel(),
                    plan.getDescription(),
                    plan.getPetLimit(),
                    plan.isMarketplaceEnabled(),
                    plan.getFeatures(),
                    plan.getMetrics()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
