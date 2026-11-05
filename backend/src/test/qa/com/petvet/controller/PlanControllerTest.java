package com.petvet.controller;

import com.petvet.dto.PlanResponse;
import com.petvet.platform.ProductPlanTier;
import com.petvet.service.PlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllPlansReturnsThreeTiers() throws Exception {
        mockMvc.perform(get("/api/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].tier", is("FREE")))
                .andExpect(jsonPath("$[1].tier", is("PRO")))
                .andExpect(jsonPath("$[2].tier", is("PREMIUM")));
    }

    @Test
    void getPlanByTierReturnsFreePlan() throws Exception {
        mockMvc.perform(get("/api/plans/FREE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier", is("FREE")))
                .andExpect(jsonPath("$.petLimit", is(3)))
                .andExpect(jsonPath("$.marketplaceEnabled", is(false)))
                .andExpect(jsonPath("$.features", hasSize(greaterThan(0))));
    }

    @Test
    void getPlanByTierReturnsProPlan() throws Exception {
        mockMvc.perform(get("/api/plans/PRO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tier", is("PRO")))
                .andExpect(jsonPath("$.petLimit", is(1000)))
                .andExpect(jsonPath("$.marketplaceEnabled", is(true)))
                .andExpect(jsonPath("$.metrics", hasSize(greaterThan(0))));
    }

    @Test
    void getPlanByInvalidTierReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/plans/INVALID"))
                .andExpect(status().isNotFound());
    }
}
