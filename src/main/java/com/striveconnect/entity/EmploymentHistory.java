package com.striveconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employment_history")
public class EmploymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String jobTitle;
    
    private String location;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    private LocalDate endDate; // Null if current job

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    
    @Column(name = "engagement_id")
    private String engagementId;
    
    // --- NEW FIELDS ---
    
    public String getEngagementId() {
		return engagementId;
	}

	public void setEngagementId(String engagementId) {
		this.engagementId = engagementId;
	}
	/**
     * Type of proof provided (e.g., "OFFER_LETTER", "SALARY_SLIP").
     */
    private String attachmentType; 

    /**
     * The URL to the stored file, returned by the FileService.
     * e.g., "STRIVE/user-uuid/offer_letter.pdf"
     */
    private String attachmentUrl;

    /**
     * Remarks from the admin if the status is REJECTED.
     */
    @Lob
    private String adminRemarks;
    
    // --- END OF NEW FIELDS ---

    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    public enum VerificationStatus {
        PENDING_VERIFICATION,
        VERIFIED,
        REJECTED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = VerificationStatus.PENDING_VERIFICATION;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    
    @Column(nullable = false)
    private String tenantId;
   

    // --- Getters and Setters for all fields ---

    public Long getEmploymentId() { return employmentId; }
    public void setEmploymentId(Long employmentId) { this.employmentId = employmentId; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
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
    public String getAttachmentType() { return attachmentType; }
    public void setAttachmentType(String attachmentType) { this.attachmentType = attachmentType; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}
