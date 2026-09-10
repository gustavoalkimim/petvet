package com.petvet.repository;

import com.petvet.model.PetHealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetHealthRecordRepository extends JpaRepository<PetHealthRecord, Long> {
    List<PetHealthRecord> findByPetId(Long petId);
}
