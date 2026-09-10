package com.petvet.controller;

import com.petvet.model.Clinic;
import com.petvet.security.AuthzService;
import com.petvet.service.ClinicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {

    private final ClinicService clinicService;
    private final AuthzService authzService;

    public ClinicController(ClinicService clinicService, AuthzService authzService) {
        this.clinicService = clinicService;
        this.authzService = authzService;
    }

    @GetMapping
    public ResponseEntity<List<Clinic>> listAll(Authentication authentication) {
        if (authentication == null || !authzService.canAccessClinic(authentication, null)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(clinicService.findAll());
    }

    @PostMapping
    public ResponseEntity<Clinic> create(@RequestBody Clinic clinic, Authentication authentication) {
        if (authentication == null || !authzService.canAccessClinic(authentication, clinic.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(clinicService.save(clinic));
    }
}
