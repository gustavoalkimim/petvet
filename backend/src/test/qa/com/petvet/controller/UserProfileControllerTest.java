package com.petvet.controller;

import com.petvet.service.UserProfileService;
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
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProfileWithoutAuthenticationReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getProfileWithAuthenticationReturnsProfile() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("PET_OWNER")))
                .andExpect(jsonPath("$.planTier", is("FREE")))
                .andExpect(jsonPath("$.petCount", is(2)))
                .andExpect(jsonPath("$.marketplaceAccess", is(false)));
    }

    @Test
    @WithMockUser
    void getDashboardWithAuthenticationReturnsDashboard() throws Exception {
        mockMvc.perform(get("/api/user/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.metrics", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.highlights", hasSize(greaterThan(0))));
    }

    @Test
    void getDashboardWithoutAuthenticationReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/user/dashboard"))
                .andExpect(status().isForbidden());
    }
}
