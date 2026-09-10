package com.petvet.service;

import com.petvet.model.Clinic;
import com.petvet.repository.ClinicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClinicServiceTest {

    @Mock
    private ClinicRepository clinicRepository;

    @InjectMocks
    private ClinicService clinicService;

    @Test
    void findAll_shouldReturnAllClinicsFromRepository() {
        List<Clinic> expected = List.of(new Clinic(), new Clinic());

        when(clinicRepository.findAll()).thenReturn(expected);

        List<Clinic> actual = clinicService.findAll();

        assertEquals(expected, actual);
        verify(clinicRepository).findAll();
    }

    @Test
    void save_shouldPersistClinicAndReturnSavedEntity() {
        Clinic clinic = new Clinic();
        when(clinicRepository.save(clinic)).thenReturn(clinic);

        Clinic saved = clinicService.save(clinic);

        assertEquals(clinic, saved);
        verify(clinicRepository).save(clinic);
    }
}
