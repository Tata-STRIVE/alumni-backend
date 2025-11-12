package com.striveconnect.dto;

import com.striveconnect.entity.EmploymentHistory;
import com.striveconnect.entity.EmploymentHistory.VerificationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmploymentHistoryDto {

    private Long employmentId;
    private String companyName;
    private String jobTitle;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private VerificationStatus status;
    private String engagementId;
    private String attachmentType;
    private String attachmentUrl;
    private String adminRemarks;
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EmploymentHistoryDto fromEntity(EmploymentHistory e) {
        if (e == null) return null;

        EmploymentHistoryDto dto = new EmploymentHistoryDto();
        dto.setEmploymentId(e.getEmploymentId());
        dto.setCompanyName(e.getCompanyName());
        dto.setJobTitle(e.getJobTitle());
        dto.setLocation(e.getLocation());
        dto.setStartDate(e.getStartDate());
        dto.setEndDate(e.getEndDate());
        dto.setStatus(e.getStatus());
        dto.setEngagementId(e.getEngagementId());
        dto.setAttachmentType(e.getAttachmentType());
        dto.setAttachmentUrl(e.getAttachmentUrl());
        dto.setAdminRemarks(e.getAdminRemarks());
        dto.setTenantId(e.getTenantId());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    // --- Getters & Setters ---
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

    public VerificationStatus getStatus() { return status; }
    public void setStatus(VerificationStatus status) { this.status = status; }

    public String getEngagementId() { return engagementId; }
    public void setEngagementId(String engagementId) { this.engagementId = engagementId; }

    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
