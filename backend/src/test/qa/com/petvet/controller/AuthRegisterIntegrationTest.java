package com.petvet.controller;

import com.petvet.model.User;
import com.petvet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthRegisterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private static String body(String email, String document) {
        return """
            {"name":"Ana Souza","email":"%s","password":"Senha-Forte-123",
             "clinicName":"Clínica Bicho Feliz","clinicDocument":"%s"}
            """.formatted(email, document);
    }

    @Test
    void registerPersistsUserLinkedToNewClinicAndReturnsTokens() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("ana@bichofeliz.com", "12.345.678/0001-99")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", notNullValue()))
            .andExpect(jsonPath("$.refreshToken", notNullValue()));

        User saved = userRepository.findByEmail("ana@bichofeliz.com").orElseThrow();
        assertNotNull(saved.getClinic());
        assertNotNull(saved.getClinic().getId());
        assertEquals("12345678000199", saved.getClinic().getDocument());
        assertEquals("EMPLOYEE", saved.getRole());
    }

    @Test
    void registerRejectsSecondClinicWithSameDocument() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("primeira@clinica.com", "98765432000110")))
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body("segunda@clinica.com", "98.765.432/0001-10")))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Clinic document already registered."));
    }

    @Test
    void registerRequiresClinicFields() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"name":"Ana","email":"ana@x.com","password":"Senha-Forte-123"}
                    """))
            .andExpect(status().isBadRequest());
    }
}
