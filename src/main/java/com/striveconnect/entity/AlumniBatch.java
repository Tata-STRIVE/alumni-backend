package com.striveconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alumni_batches")
public class AlumniBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="alumni_batch_id")
    private Long id;

    /**
     * The alumnus (user) who attended the batch.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumnus_user_id", nullable = false)
    private User alumnus;

    /**
     * The Batch entity this attendance refers to.
     * This must exist so code can call alumniBatch.getBatch().
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    /**
     * Engagement id (external system id) for this attendance instance.
     * One alumnus can have multiple engagement ids (multiple attendances).
     */
    @Column(name = "engagement_id")
    private String engagementId;

    // Additional optional fields
    private String certificateUrl;
    private Boolean certificateIssued = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --------------------
    // Getters & Setters
    // --------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getAlumnus() { return alumnus; }
    public void setAlumnus(User alumnus) { this.alumnus = alumnus; }

    public Batch getBatch() { return batch; }
    public void setBatch(Batch batch) { this.batch = batch; }

    public String getEngagementId() { return engagementId; }
    public void setEngagementId(String engagementId) { this.engagementId = engagementId; }

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public Boolean getCertificateIssued() { return certificateIssued; }
    public void setCertificateIssued(Boolean certificateIssued) { this.certificateIssued = certificateIssued; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
