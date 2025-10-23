package com.striveconnect.repository;

import com.striveconnect.entity.EmploymentHistory;
import com.striveconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, Long> {

    List<EmploymentHistory> findByUser(User user);
    
    @Query("SELECT eh FROM EmploymentHistory eh WHERE eh.user.tenantId = :tenantId AND eh.status = :status")
    List<EmploymentHistory> findByTenantIdAndStatus(String tenantId, EmploymentHistory.VerificationStatus status);

    /**
     * NEW: Finds the current "Present" job for a user (where endDate is null).
     */
    Optional<EmploymentHistory> findByUserAndEndDateIsNull(User user);
}

