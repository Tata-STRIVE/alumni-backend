package com.striveconnect.repository;

import com.striveconnect.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    // Find all feedback for a specific item (e.g., a course) within a tenant
    List<Feedback> findByTenantIdAndFeedbackTypeAndRelatedId(String tenantId, Feedback.FeedbackType type, Long relatedId);
}
