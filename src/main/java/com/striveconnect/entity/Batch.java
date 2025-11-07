		package com.striveconnect.entity;
		
		import jakarta.persistence.*;
		import java.time.LocalDate;
		import java.util.List;
		
		import com.striveconnect.repository.UserRepository;
		
		/**
		 * UPDATED Entity for the `batches` table.
		 * Now includes specific dates and a status.
		 */
		@Entity
		@Table(name = "batches")
		public class Batch  {
		
		  
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
		
		
		    @ManyToOne(fetch = FetchType.LAZY)
		    @JoinColumn(name = "created_by_user_id", nullable = false)
		    private User createdBy; // Alumnus or Admin who created it
		
			public User getCreatedBy() {
				return createdBy;
			}
		
			public void setCreatedBy(User createdBy) {
				this.createdBy = createdBy;
			}
		
			public LocalDate getCreatedAt() {
				return createdAt;
			}
		
			public void setCreatedAt(LocalDate createdAt) {
				this.createdAt = createdAt;
			}
			@Column(updatable = false)
		    private LocalDate createdAt;
		
		    @ManyToOne(fetch = FetchType.LAZY)
		    @JoinColumn(name = "updated_by_user_id")
		    private User updatedBy;
		
		    private LocalDate updatedAt;
		    
		    @Column(nullable = false)
		    private boolean isActive = true;
		
		    public boolean isActive() {
		        return isActive;
		    }
		
		    public void setActive(boolean active) {
		        this.isActive = active;
		    }
		
		    public User getUpdatedBy() {
		        return updatedBy;
		        	
		    }
		
		    public void setUpdatedBy(User updatedBy) {
		        this.updatedBy = updatedBy;
		    }
		
		    public LocalDate getUpdatedAt() {
		        return updatedAt;
		    }
		
		    public void setUpdatedAt(LocalDate updatedAt) {
		        this.updatedAt = updatedAt;
		    }
		
		  
		
		
		    @PrePersist
		    protected void onCreate() {
		        this.createdAt = LocalDate.now();
		        this.isActive = true;
		    }
		
		    @PreUpdate
		    protected void onUpdate() {
		        this.updatedAt = LocalDate.now();
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
		
