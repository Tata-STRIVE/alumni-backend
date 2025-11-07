package com.striveconnect.dto;

import java.time.LocalDate;

/**
 * Represents a single batch, with flattened names for course and center.
 * This is used for the "upcoming batches" list and the registration dropdowns.
 */
public class BatchDto {

    private Long batchId;
    private String batchName; // The new field you added
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    
    // Flattened data from related tables
    private String courseName;
    private String centerName;
    private long courseId;
    private long centerId;
    public long getCourseId() {
		return courseId;
	}
	public void setCourseId(long courseId) {
		this.courseId = courseId;
	}
	public long getCenterId() {
		return centerId;
	}
	public void setCenterId(long centerId) {
		this.centerId = centerId;
	}
	private String centerCity;

    // Getters and Setters
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCenterName() { return centerName; }
    public void setCenterName(String centerName) { this.centerName = centerName; }
    public String getCenterCity() { return centerCity; }
    public void setCenterCity(String centerCity) { this.centerCity = centerCity; }
}

