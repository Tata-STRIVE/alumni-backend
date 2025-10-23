package com.striveconnect.repository;

import com.striveconnect.entity.UpskillingApplication;
import com.striveconnect.entity.UpskillingOpportunity;
import com.striveconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpskillingApplicationRepository extends JpaRepository<UpskillingApplication, Long> {
    
    boolean existsByOpportunityAndUser(UpskillingOpportunity opportunity, User user);

    List<UpskillingApplication> findByOpportunity(UpskillingOpportunity opportunity);
    
    // NEW: Find all applications submitted by a specific user
    List<UpskillingApplication> findByUser(User user);
}

