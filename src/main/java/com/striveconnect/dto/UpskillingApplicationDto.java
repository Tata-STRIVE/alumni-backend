package com.striveconnect.dto;

import java.time.LocalDateTime;

/**
 * DTO for displaying an Alumnus's upskilling application status.
 */
public class UpskillingApplicationDto {
    private Long applicationId;
    private String status;
    private LocalDateTime appliedAt;
    
    // Details of the course they applied for
    private Long opportunityId;
    private String opportunityTitle;

    // Getters and Setters
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public Long getOpportunityId() { return opportunityId; }
    public void setOpportunityId(Long opportunityId) { this.opportunityId = opportunityId; }
    public String getOpportunityTitle() { return opportunityTitle; }
    public void setOpportunityTitle(String opportunityTitle) { this.opportunityTitle = opportunityTitle; }
}

