package com.petvet.platform;

import java.util.List;

public class ProductPlan {

    private final ProductPlanTier tier;
    private final String label;
    private final String description;
    private final int petLimit;
    private final boolean marketplaceEnabled;
    private final List<String> features;
    private final List<String> metrics;

    public ProductPlan(ProductPlanTier tier, String label, String description, int petLimit,
                       boolean marketplaceEnabled, List<String> features, List<String> metrics) {
        this.tier = tier;
        this.label = label;
        this.description = description;
        this.petLimit = petLimit;
        this.marketplaceEnabled = marketplaceEnabled;
        this.features = List.copyOf(features);
        this.metrics = List.copyOf(metrics);
    }

    public ProductPlanTier getTier() {
        return tier;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public int getPetLimit() {
        return petLimit;
    }

    public boolean isMarketplaceEnabled() {
        return marketplaceEnabled;
    }

    public List<String> getFeatures() {
        return features;
    }

    public List<String> getMetrics() {
        return metrics;
    }
}
