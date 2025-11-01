package com.striveconnect.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for an Admin to approve or reject a pending item.
 */
public class AdminReviewDto {

    @NotBlank(message = "Status cannot be blank")
    private String status; // "VERIFIED" or "REJECTED"

    private String remarks; // Required only if status is "REJECTED"

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

