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

    List<EmploymentHistory> findByAuthorOrderByStartDateDesc(User author);
    
    Optional<EmploymentHistory> findByAuthorAndEndDateIsNull(User author);

    @Query("SELECT eh FROM EmploymentHistory eh WHERE eh.author.tenantId = :tenantId AND eh.status = :status ORDER BY eh.createdAt ASC")
    List<EmploymentHistory> findByTenantAndStatus(String tenantId, EmploymentHistory.VerificationStatus status);
}

