package com.striveconnect.repository;

import com.striveconnect.entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    
  
    /**
     * Finds all jobs for a tenant that are ACTIVE (approved and visible).
     */
    @Query("SELECT j FROM JobPosting j WHERE j.tenantId = :tenantId AND j.status = :status ORDER BY j.createdAt DESC")
    List<JobPosting> findByTenantIdAndStatus(String tenantId, JobPosting.JobStatus status);

    /**
     * Finds all jobs for a tenant, regardless of status (for Admins).
     */
    @Query("SELECT j FROM JobPosting j WHERE j.tenantId = :tenantId ORDER BY j.createdAt DESC")
    List<JobPosting> findAllByTenantId(String tenantId);
}

