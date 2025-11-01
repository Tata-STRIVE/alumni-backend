package com.striveconnect.dto;

import java.time.LocalDate;

/**
 * DTO for Employment History, now includes attachments and remarks.
 * This is used for both Alumnus and Admin.
 */
public class EmploymentHistoryDto {

    private Long employmentId;
    private String companyName;
    private String jobTitle;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // "PENDING_VERIFICATION", "VERIFIED", "REJECTED"
    private String authorName; // The Alumnus who submitted it (for Admin view)

    // --- NEW FIELDS ---
    private String attachmentType;
    private String attachmentUrl; // Relative path to the file
    private String adminRemarks;  // Admin feedback if REJECTED
    // --- END OF NEW FIELDS ---

    // Getters and Setters
    public Long getEmploymentId() { return employmentId; }
    public void setEmploymentId(Long employmentId) { this.employmentId = employmentId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }
}

