package com.striveconnect.dto;

import java.time.LocalDateTime;

/**
 * DTO for displaying an Alumnus's job application status.
 */
public class JobApplicationDto {
    private Long applicationId;
    private String status;
    private LocalDateTime appliedAt;
    
    // Details of the job they applied for
    private Long jobId;
    private String jobTitle;
    private String companyName;

    // Getters and Setters
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
