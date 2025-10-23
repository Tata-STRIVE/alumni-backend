package com.striveconnect.dto;

/**
 * Data Transfer Object for creating a new job posting by an Admin.
 * Contains only the fields necessary to create a new job.
 */
public class CreateJobRequestDto {
    private String title;
    private String companyName;
    private String location;
    private String description;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
