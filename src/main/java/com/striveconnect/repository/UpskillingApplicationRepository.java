package com.striveconnect.repository;

import com.striveconnect.entity.UpskillingApplication;
import com.striveconnect.entity.UpskillingOpportunity;
import com.striveconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpskillingApplicationRepository extends JpaRepository<UpskillingApplication, Long> {
    
    boolean existsByOpportunityAndUser(UpskillingOpportunity opportunity, User user);

    @Query("SELECT a FROM UpskillingApplication a WHERE a.opportunity.id = :opportunityId AND a.status = 'APPLIED'")
    List<UpskillingApplication> findByOpportunityIdAndStatus(@Param("opportunityId") Long opportunityId);    
    // NEW: Find all applications submitted by a specific user
    List<UpskillingApplication> findByUser(User user);
}

