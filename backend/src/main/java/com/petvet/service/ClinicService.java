package com.petvet.service;

import com.petvet.model.Clinic;
import com.petvet.repository.ClinicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicService {

    private final ClinicRepository clinicRepository;

    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    public List<Clinic> findAll() {
        return clinicRepository.findAll();
    }

    public Clinic save(Clinic clinic) {
        return clinicRepository.save(clinic);
    }
}
