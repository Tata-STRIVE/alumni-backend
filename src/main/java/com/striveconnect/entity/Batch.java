package com.striveconnect.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * UPDATED Entity for the `batches` table.
 * Now includes specific dates and a status.
 */
@Entity
@Table(name = "batches")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long batchId;

    @Column(nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private String batchName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center center;

    private LocalDate startDate; // Changed from startYear
    private LocalDate endDate;   // New
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status; // New

    @OneToMany(mappedBy = "batch")
    private List<User> users;
    
    public enum BatchStatus {
        UPCOMING,
        IN_PROGRESS,
        COMPLETED
    }

    // Getters and Setters
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    
    
    
    public String getBatchName() {
		return batchName;
	}
	public void setBatchName(String batchName) {
		this.batchName = batchName;
	}
	public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Center getCenter() { return center; }
    public void setCenter(Center center) { this.center = center; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BatchStatus getStatus() { return status; }
    public void setStatus(BatchStatus status) { this.status = status; }
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}

