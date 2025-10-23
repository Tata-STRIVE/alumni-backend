package com.striveconnect.dto;

import java.time.LocalDate;

// Using a single file for related DTOs for this module

/**
 * DTO for viewing an upskilling opportunity.
 */
public class UpskillingOpportunityDto {
    private Long opportunityId;
    private String title;
    private String description;
    private String prerequisites;
    private LocalDate startDate;
    
    // Getters and Setters
    public Long getOpportunityId() { return opportunityId; }
    public void setOpportunityId(Long opportunityId) { this.opportunityId = opportunityId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
}

