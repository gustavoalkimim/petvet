package com.petvet.service;

import com.petvet.model.Pet;
import com.petvet.repository.PetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<Pet> findByClinic(Long clinicId) {
        return petRepository.findByClinicId(clinicId);
    }

    public Pet save(Pet pet) {
        return petRepository.save(pet);
    }
}
