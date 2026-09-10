package com.petvet.dto;

import java.util.List;

public class DashboardResponse {
    private String title;
    private List<String> metrics;
    private List<String> highlights;
    private boolean marketplaceEnabled;

    public DashboardResponse(String title, List<String> metrics, List<String> highlights,
                            boolean marketplaceEnabled) {
        this.title = title;
        this.metrics = metrics;
        this.highlights = highlights;
        this.marketplaceEnabled = marketplaceEnabled;
    }

    public String getTitle() { return title; }
    public List<String> getMetrics() { return metrics; }
    public List<String> getHighlights() { return highlights; }
    public boolean isMarketplaceEnabled() { return marketplaceEnabled; }
}
