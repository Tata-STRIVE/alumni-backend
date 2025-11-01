package com.striveconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for the `job_postings` table.
 * Now includes mandatory HR contact info, a robust status enum,
 * and rejection remarks for the admin review process.
 */
@Entity
@Table(name = "job_postings")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @Column(nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String location;

    @Lob
    @Column(nullable = false)
    private String description;

    // --- NEW: Mandatory HR Contact Info ---
    private String hrContactEmail;

    private String hrContactPhone;

    // --- NEW: Robust Status Enum ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    // --- NEW: Admin Review Fields ---
    @Lob
    private String rejectionRemarks; // Mandatory if status is REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_admin_id")
    private User reviewedBy; // Admin who approved/rejected

    private LocalDateTime reviewedAt;

    // --- Author Details ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by_user_id", nullable = false)
    private User author; // Alumnus or Admin who created it

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum JobStatus {
    	PENDING_VERIFICATION, // Submitted by Alumnus
        OPEN,           // Approved by Admin or Posted by Admin
        REJECTED,         // Rejected by Admin
        CLOSED            // Closed/Filled
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // --- Getters and Setters for all fields ---

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHrContactEmail() {
        return hrContactEmail;
    }

    public void setHrContactEmail(String hrContactEmail) {
        this.hrContactEmail = hrContactEmail;
    }

    public String getHrContactPhone() {
        return hrContactPhone;
    }

    public void setHrContactPhone(String hrContactPhone) {
        this.hrContactPhone = hrContactPhone;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getRejectionRemarks() {
        return rejectionRemarks;
    }

    public void setRejectionRemarks(String rejectionRemarks) {
        this.rejectionRemarks = rejectionRemarks;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

