package com.petvet.platform;

import java.util.List;

public class ProductDashboardConfig {

    private final String title;
    private final List<String> metrics;
    private final List<String> highlights;
    private final boolean marketplaceEnabled;

    public ProductDashboardConfig(String title, List<String> metrics, List<String> highlights, boolean marketplaceEnabled) {
        this.title = title;
        this.metrics = List.copyOf(metrics);
        this.highlights = List.copyOf(highlights);
        this.marketplaceEnabled = marketplaceEnabled;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getMetrics() {
        return metrics;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public boolean isMarketplaceEnabled() {
        return marketplaceEnabled;
    }
}
