package com.petvet.repository;

import com.petvet.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByPetId(Long petId);
    List<Reminder> findByStatus(String status);
}
