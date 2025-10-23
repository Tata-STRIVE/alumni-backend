package com.striveconnect.dto;

import java.time.LocalDate;

/**
 * DTO for creating a new upskilling opportunity by an Admin.
 */
public class CreateUpskillingOpportunityDto {
    private String title;
    private String description;
    private String prerequisites;
    private LocalDate startDate;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
}
