package com.striveconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "upskilling_opportunities")
public class UpskillingOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long opportunityId;

    private String tenantId;
    private String title;

    @Lob
    private String description;

    private String prerequisites;
    private LocalDate startDate;
    private LocalDate endDate;

    
    private String link;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by_user_id")
    private User postedBy;

    private LocalDateTime createdAt;
    
    // Getters and Setters
    public Long getOpportunityId() { return opportunityId; }
    public void setOpportunityId(Long opportunityId) { this.opportunityId = opportunityId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPrerequisites() { return prerequisites; }
    public void setPrerequisites(String prerequisites) { this.prerequisites = prerequisites; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public User getPostedBy() { return postedBy; }
    public void setPostedBy(User postedBy) { this.postedBy = postedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
    
    
    
}
