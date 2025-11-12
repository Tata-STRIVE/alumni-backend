package com.striveconnect.dto;


import java.time.LocalDate;

public class EngagementDto {
    private Long engagementId;
    private String studentId;       // from user table
    private Long batchId;
    private String engagementCode;  // optional external/sync ID
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;          // ACTIVE / COMPLETED
    private boolean certificateIssued;
	public Long getEngagementId() {
		return engagementId;
	}
	public void setEngagementId(Long engagementId) {
		this.engagementId = engagementId;
	}
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
	public Long getBatchId() {
		return batchId;
	}
	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	public String getEngagementCode() {
		return engagementCode;
	}
	public void setEngagementCode(String engagementCode) {
		this.engagementCode = engagementCode;
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
	public boolean isCertificateIssued() {
		return certificateIssued;
	}
	public void setCertificateIssued(boolean certificateIssued) {
		this.certificateIssued = certificateIssued;
	}
    
    
    
    
    
}
