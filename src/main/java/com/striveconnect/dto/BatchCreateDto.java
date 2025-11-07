package com.striveconnect.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a single batch, with flattened names for course and center.
 * This is used for the "upcoming batches" list and the registration dropdowns.
 */
public class BatchCreateDto {

    private String batchName; // The new field you added
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    
    // Flattened data from related tables
    private long courseId;
    private long centerId;
	public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
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
    
    
    
    
}

