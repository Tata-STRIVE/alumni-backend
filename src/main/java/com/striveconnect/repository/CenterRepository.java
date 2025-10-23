package com.striveconnect.repository;

import com.striveconnect.entity.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Center entity.
 */
@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {

    /**
     * Finds all centers associated with a specific tenant.
     *
     * @param tenantId The ID of the tenant.
     * @return A list of centers for that tenant.
     */
    List<Center> findByTenantId(String tenantId);
}
