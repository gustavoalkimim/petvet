package com.petvet.service;

import com.petvet.platform.ProductDashboardCatalog;
import com.petvet.platform.ProductDashboardConfig;
import com.petvet.platform.UserPlanProfile;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private final ProductDashboardCatalog dashboardCatalog = new ProductDashboardCatalog();

    public ProductDashboardConfig getDashboard(UserPlanProfile profile) {
        return dashboardCatalog.forUser(profile);
    }
}
