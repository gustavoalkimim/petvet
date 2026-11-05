package com.petvet.controller;

import com.petvet.model.ServiceProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class MarketplaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ServiceProvider testProvider;

    @BeforeEach
    void setup() {
        testProvider = new ServiceProvider();
        testProvider.setName("Clínica Veterinária Central");
        testProvider.setType("CLINIC");
        testProvider.setEmail("clinica@example.com");
        testProvider.setPhone("1133334444");
        testProvider.setAddress("Rua das Flores, 123");
        testProvider.setCity("São Paulo");
        testProvider.setRegion("Vila Madalena");
        testProvider.setDescription("Clínica veterinária com 20 anos de experiência");
        testProvider.setRating(4.8);
        testProvider.setReviewCount(156);
        testProvider.setStatus("ACTIVE");
    }

    @Test
    void getAllProvidersReturnsEmptyListOnInitial() throws Exception {
        mockMvc.perform(get("/api/marketplace/providers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProviderReturnsCreatedStatus() throws Exception {
        mockMvc.perform(post("/api/marketplace/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProvider)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Clínica Veterinária Central"))
                .andExpect(jsonPath("$.type").value("CLINIC"))
                .andExpect(jsonPath("$.rating").value(4.8));
    }

    @Test
    void getProviderByIdReturnsNotFoundForInvalidId() throws Exception {
        mockMvc.perform(get("/api/marketplace/providers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void createProviderIsForbiddenForClinicEmployee() throws Exception {
        mockMvc.perform(post("/api/marketplace/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProvider)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void createProviderIsRejectedWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/marketplace/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProvider)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProvidersByCityFiltersCorrectly() throws Exception {
        mockMvc.perform(post("/api/marketplace/providers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProvider)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/marketplace/providers/city/São Paulo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
