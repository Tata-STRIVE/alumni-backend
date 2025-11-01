package com.striveconnect.dto;

// DTOs for Job Postings to control data transfer
public class JobPostingDto {
    private Long jobId;
    private String title;
    private String companyName;
    private String location;
    private String description;
    private String authorName;
    
    private String hrContactEmail;
    private String hrContactPhone;
    // Author details
    private String authorId; // The ID of the user who posted it
    private String status; // e.g., "PENDING_APPROVAL", "ACTIVE", "REJECTED"
    private String rejectionRemarks;
    
    // Getters and Setters
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
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
	public String getAuthorId() {
		return authorId;
	}
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRejectionRemarks() {
		return rejectionRemarks;
	}
	public void setRejectionRemarks(String rejectionRemarks) {
		this.rejectionRemarks = rejectionRemarks;
	}
    
    
    
}
