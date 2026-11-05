package com.petvet.repository;

import com.petvet.model.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    boolean existsByDocument(String document);
}
