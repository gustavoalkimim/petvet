package com.petvet.service;

import com.petvet.model.Clinic;
import com.petvet.model.User;
import com.petvet.repository.ClinicRepository;
import com.petvet.repository.UserRepository;
import com.petvet.service.RegistrationService.RegistrationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    private static final String STRONG_PASSWORD = "Senha-Forte-123";
    private static final Set<String> ROLES_ALLOWED_BY_SCHEMA = Set.of("ADMIN", "EMPLOYEE", "SUPPORT");

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClinicRepository clinicRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void registerCreatesClinicAndLinksUserWithRoleAllowedBySchema() {
        when(userRepository.findByEmail("ana@clinica.com")).thenReturn(Optional.empty());
        when(clinicRepository.existsByDocument("12345678000199")).thenReturn(false);
        when(clinicRepository.save(any(Clinic.class))).thenAnswer(inv -> {
            Clinic clinic = inv.getArgument(0);
            clinic.setId(7L);
            return clinic;
        });
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(passwordEncoder.encode(STRONG_PASSWORD)).thenReturn("hash");

        User user = registrationService.register(
            " Ana ", " Ana@Clinica.com ", STRONG_PASSWORD, " Clínica Bicho Feliz ", "12.345.678/0001-99");

        assertEquals("ana@clinica.com", user.getEmail());
        assertEquals("Ana", user.getName());
        assertEquals("hash", user.getPasswordHash());
        assertEquals(7L, user.getClinic().getId());
        assertEquals("12345678000199", user.getClinic().getDocument());
        assertEquals("Clínica Bicho Feliz", user.getClinic().getName());
        assertTrue(ROLES_ALLOWED_BY_SCHEMA.contains(user.getRole()));
    }

    @Test
    void registerNeverGrantsGlobalAdmin() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(clinicRepository.save(any(Clinic.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User user = registrationService.register("Ana", "ana@clinica.com", STRONG_PASSWORD, "Clínica", "12345678901");

        assertNotEquals("ADMIN", user.getRole());
    }

    @Test
    void registerRejectsDuplicatedEmailWithoutCreatingClinic() {
        when(userRepository.findByEmail("ana@clinica.com")).thenReturn(Optional.of(new User()));

        var ex = assertThrows(RegistrationException.class, () ->
            registrationService.register("Ana", "ana@clinica.com", STRONG_PASSWORD, "Clínica", "12345678000199"));

        assertEquals("Email already registered.", ex.getMessage());
        verify(clinicRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerRejectsDuplicatedClinicDocument() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(clinicRepository.existsByDocument("12345678000199")).thenReturn(true);

        var ex = assertThrows(RegistrationException.class, () ->
            registrationService.register("Ana", "ana@clinica.com", STRONG_PASSWORD, "Clínica", "12345678000199"));

        assertEquals("Clinic document already registered.", ex.getMessage());
        verify(clinicRepository, never()).save(any());
    }

    @Test
    void registerRejectsWeakPasswordAndInvalidDocument() {
        assertThrows(RegistrationException.class, () ->
            registrationService.register("Ana", "ana@clinica.com", "fraca", "Clínica", "12345678000199"));
        assertThrows(RegistrationException.class, () ->
            registrationService.register("Ana", "ana@clinica.com", STRONG_PASSWORD, "Clínica", "123"));

        verifyNoInteractions(clinicRepository);
    }
}
