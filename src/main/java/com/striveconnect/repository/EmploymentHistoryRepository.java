package com.striveconnect.repository;

import com.striveconnect.entity.EmploymentHistory;
import com.striveconnect.entity.User;
import com.striveconnect.entity.EmploymentHistory.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmploymentHistoryRepository extends JpaRepository<EmploymentHistory, Long> {
    List<EmploymentHistory> findByAuthor(User author);
    List<EmploymentHistory> findByStatus(VerificationStatus status);
}
