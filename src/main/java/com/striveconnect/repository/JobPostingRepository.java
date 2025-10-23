package com.striveconnect.repository;

import com.striveconnect.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    
    /**
     * Finds all open job postings for a specific tenant.
     * @param tenantId The tenant to search within.
     * @param status The desired job status (e.g., OPEN).
     * @return A list of matching job postings.
     */
    List<JobPosting> findByTenantIdAndStatus(String tenantId, JobPosting.JobStatus status);
}

