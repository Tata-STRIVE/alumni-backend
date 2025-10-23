package com.striveconnect.dto;

import java.time.LocalDate;

public class EmploymentHistoryDto {
    private Long employmentId;
    private String companyName;
    private String jobTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String authorName;
    private String location; // --- NEW FIELD ---

    // Getters and Setters
    public Long getEmploymentId() { return employmentId; }
    public void setEmploymentId(Long employmentId) { this.employmentId = employmentId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}

