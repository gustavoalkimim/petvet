package com.petvet.service;

import com.petvet.model.ServiceProvider;
import com.petvet.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceProviderService {

    private final ServiceProviderRepository repository;

    public ServiceProviderService(ServiceProviderRepository repository) {
        this.repository = repository;
    }

    public ServiceProvider save(ServiceProvider provider) {
        return repository.save(provider);
    }

    public Optional<ServiceProvider> findById(Long id) {
        return repository.findById(id);
    }

    public List<ServiceProvider> findAll() {
        return repository.findAll();
    }

    public List<ServiceProvider> findActiveByCity(String city) {
        return repository.findActiveByCity(city);
    }

    public List<ServiceProvider> findActiveByTypeAndRegion(String type, String region) {
        return repository.findActiveByTypeAndRegion(type, region);
    }

    public List<ServiceProvider> findByRegion(String region) {
        return repository.findByRegion(region);
    }
}
