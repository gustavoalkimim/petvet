package com.petvet.controller;

import com.petvet.model.Pet;
import com.petvet.service.PetService;
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
class PetControllerTest {

    @Mock
    private PetService petService;

    @InjectMocks
    private PetController petController;

    @Test
    void listByClinic_shouldReturnOkWithListOfPets() {
        Long clinicId = 15L;
        List<Pet> pets = List.of(new Pet(), new Pet());

        when(petService.findByClinic(clinicId)).thenReturn(pets);
        var auth = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        "user@example.com",
                        "pass",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        ResponseEntity<List<Pet>> response = petController.listByClinic(clinicId, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pets, response.getBody());
        verify(petService).findByClinic(clinicId);
    }

    @Test
    void create_shouldReturnOkWithSavedPet() {
        Pet pet = new Pet();
        pet.setClinic(new com.petvet.model.Clinic());
        pet.getClinic().setId(7L);
        when(petService.save(pet)).thenReturn(pet);
        var auth = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        "user@example.com",
                        "pass",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        ResponseEntity<Pet> response = petController.create(pet, auth);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pet, response.getBody());
        verify(petService).save(pet);
    }
}
