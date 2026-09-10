package com.petvet.controller;

import com.petvet.model.ServiceProvider;
import com.petvet.service.ServiceProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
public class MarketplaceController {

    private final ServiceProviderService serviceProviderService;

    public MarketplaceController(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @GetMapping("/providers")
    public ResponseEntity<List<ServiceProvider>> getAllProviders() {
        return ResponseEntity.ok(serviceProviderService.findAll());
    }

    @GetMapping("/providers/city/{city}")
    public ResponseEntity<List<ServiceProvider>> getProvidersByCity(@PathVariable String city) {
        return ResponseEntity.ok(serviceProviderService.findActiveByCity(city));
    }

    @GetMapping("/providers/type/{type}/region/{region}")
    public ResponseEntity<List<ServiceProvider>> getProvidersByTypeAndRegion(
            @PathVariable String type,
            @PathVariable String region) {
        return ResponseEntity.ok(serviceProviderService.findActiveByTypeAndRegion(type, region));
    }

    @GetMapping("/providers/region/{region}")
    public ResponseEntity<List<ServiceProvider>> getProvidersByRegion(@PathVariable String region) {
        return ResponseEntity.ok(serviceProviderService.findByRegion(region));
    }

    @PostMapping("/providers")
    public ResponseEntity<ServiceProvider> createProvider(@RequestBody ServiceProvider provider) {
        ServiceProvider saved = serviceProviderService.save(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<ServiceProvider> getProviderById(@PathVariable Long id) {
        return serviceProviderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
