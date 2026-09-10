package com.petvet.controller;

import com.petvet.model.Clinic;
import com.petvet.service.ClinicService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicControllerTest {

    @Mock
    private ClinicService clinicService;

    @InjectMocks
    private ClinicController clinicController;

    @Test
    void listAll_shouldReturnOkWithAllClinics() {
        List<Clinic> clinics = List.of(new Clinic(), new Clinic());

        when(clinicService.findAll()).thenReturn(clinics);
        var auth = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        "admin@example.com",
                        "pass",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        ResponseEntity<List<Clinic>> response = clinicController.listAll(auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clinics, response.getBody());
        verify(clinicService).findAll();
    }

    @Test
    void create_shouldReturnOkWithSavedClinic() {
        Clinic clinic = new Clinic();
        clinic.setId(12L);
        when(clinicService.save(clinic)).thenReturn(clinic);
        var auth = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        "admin@example.com",
                        "pass",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        ResponseEntity<Clinic> response = clinicController.create(clinic, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(clinic, response.getBody());
        verify(clinicService).save(clinic);
    }
}
