package com.striveconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "upskilling_applications")
public class UpskillingApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private UpskillingOpportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime appliedAt;

    // Reusing the same enum as JobApplication for consistency
    public enum ApplicationStatus {
        APPLIED,
        SHORTLISTED,
        REJECTED,
        SELECTED
    }

    // Getters and Setters
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public UpskillingOpportunity getOpportunity() { return opportunity; }
    public void setOpportunity(UpskillingOpportunity opportunity) { this.opportunity = opportunity; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
}
