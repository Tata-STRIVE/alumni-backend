package com.striveconnect.repository;

import com.striveconnect.entity.UpskillingOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpskillingOpportunityRepository extends JpaRepository<UpskillingOpportunity, Long> {

    // Find all opportunities for a specific tenant
    List<UpskillingOpportunity> findByTenantId(String tenantId);
}
