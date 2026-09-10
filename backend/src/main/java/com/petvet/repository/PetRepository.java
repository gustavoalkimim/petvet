package com.petvet.repository;

import com.petvet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByClinicId(Long clinicId);
    List<Pet> findByTutorId(Long tutorId);
}
