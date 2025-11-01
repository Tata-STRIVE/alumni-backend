package com.striveconnect.dto;

/**
 * DTO for an Admin to review an alumni-submitted job.
 * Requires a status and mandatory remarks if rejected.
 */
public class JobReviewDto {

    private String status; // "ACTIVE" or "REJECTED"
    private String rejectionRemarks; // Mandatory if status is "REJECTED"
    
  

    // Getters and Setters

    
    
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

