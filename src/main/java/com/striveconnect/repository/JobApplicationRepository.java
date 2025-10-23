package com.striveconnect.repository;

import com.striveconnect.entity.JobApplication;
import com.striveconnect.entity.JobPosting;
import com.striveconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByJobPostingAndUser(JobPosting jobPosting, User user);
    
    List<JobApplication> findByJobPosting(JobPosting jobPosting);
    
    // NEW: Find all applications submitted by a specific user
    List<JobApplication> findByUser(User user);
}

