package com.petvet.service;

import com.petvet.model.Pet;
import com.petvet.repository.PetRepository;
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
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    @Test
    void findByClinic_shouldReturnPetsFromRepository() {
        Long clinicId = 7L;
        List<Pet> expected = List.of(new Pet(), new Pet());

        when(petRepository.findByClinicId(clinicId)).thenReturn(expected);

        List<Pet> actual = petService.findByClinic(clinicId);

        assertEquals(expected, actual);
        verify(petRepository).findByClinicId(clinicId);
    }

    @Test
    void save_shouldPersistPetAndReturnSavedEntity() {
        Pet pet = new Pet();
        when(petRepository.save(pet)).thenReturn(pet);

        Pet saved = petService.save(pet);

        assertEquals(pet, saved);
        verify(petRepository).save(pet);
    }
}
