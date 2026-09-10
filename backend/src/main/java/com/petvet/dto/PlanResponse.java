package com.petvet.dto;

import com.petvet.platform.ProductPlanTier;
import java.util.List;

public class PlanResponse {
    private ProductPlanTier tier;
    private String label;
    private String description;
    private int petLimit;
    private boolean marketplaceEnabled;
    private List<String> features;
    private List<String> metrics;

    public PlanResponse(ProductPlanTier tier, String label, String description, int petLimit,
                        boolean marketplaceEnabled, List<String> features, List<String> metrics) {
        this.tier = tier;
        this.label = label;
        this.description = description;
        this.petLimit = petLimit;
        this.marketplaceEnabled = marketplaceEnabled;
        this.features = features;
        this.metrics = metrics;
    }

    public ProductPlanTier getTier() { return tier; }
    public String getLabel() { return label; }
    public String getDescription() { return description; }
    public int getPetLimit() { return petLimit; }
    public boolean isMarketplaceEnabled() { return marketplaceEnabled; }
    public List<String> getFeatures() { return features; }
    public List<String> getMetrics() { return metrics; }
}
