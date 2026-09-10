package com.petvet.controller;

import com.petvet.model.Pet;
import com.petvet.security.AuthzService;
import com.petvet.service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;
    private final AuthzService authzService;

    public PetController(PetService petService, AuthzService authzService) {
        this.petService = petService;
        this.authzService = authzService;
    }

    @GetMapping("/clinic/{clinicId}")
    public ResponseEntity<List<Pet>> listByClinic(@PathVariable Long clinicId, Authentication authentication) {
        if (authentication == null || !authzService.canAccessClinic(authentication, clinicId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(petService.findByClinic(clinicId));
    }

    @PostMapping
    public ResponseEntity<Pet> create(@RequestBody Pet pet, Authentication authentication) {
        if (authentication == null || !authzService.canAccessPet(authentication, pet.getClinic() != null ? pet.getClinic().getId() : null, pet.getClinic() != null ? pet.getClinic().getId() : null)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(petService.save(pet));
    }
}
