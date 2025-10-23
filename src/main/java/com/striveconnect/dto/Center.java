package com.striveconnect.dto;

/**
 * Data Transfer Object for displaying an application to an Admin.
 * Provides a summary of the applicant and their application status.
 */
public class Center {
    private Long applicationId;
    private String applicantName;
    private String applicantMobile;
    private String status;

    // Getters and Setters
    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getApplicantMobile() { return applicantMobile; }
    public void setApplicantMobile(String applicantMobile) { this.applicantMobile = applicantMobile; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
