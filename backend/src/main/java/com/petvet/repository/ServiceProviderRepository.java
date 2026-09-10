package com.petvet.repository;

import com.petvet.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    List<ServiceProvider> findByStatus(String status);

    List<ServiceProvider> findByType(String type);

    List<ServiceProvider> findByCity(String city);

    List<ServiceProvider> findByRegion(String region);

    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.status = 'ACTIVE' AND sp.city = :city ORDER BY sp.rating DESC")
    List<ServiceProvider> findActiveByCity(@Param("city") String city);

    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.status = 'ACTIVE' AND sp.type = :type AND sp.region = :region ORDER BY sp.rating DESC")
    List<ServiceProvider> findActiveByTypeAndRegion(@Param("type") String type, @Param("region") String region);
}
