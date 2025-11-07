package com.striveconnect.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity for the `courses` table.
 * This table now holds only the core, non-translatable course data.
 */
/**
 * 
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(nullable = false)
    private String tenantId;

    private String iconUrl;

    // A course can have many translations
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CourseTranslation> translations;
    
    
    // A course can be part of many batches
    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Batch> batches;
    
    @Column(nullable = true)
    private String name;
    private String description;
    // --- Author Details ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy; // Alumnus or Admin who created it

   

	@Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private boolean isActive = true;

      // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public List<CourseTranslation> getTranslations() { return translations; }
    public void setTranslations(List<CourseTranslation> translations) { this.translations = translations; }
    public List<Batch> getBatches() { return batches; }
    public void setBatches(List<Batch> batches) { this.batches = batches; }
    
    
    
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
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

	    public LocalDateTime getUpdatedAt() {
	        return updatedAt;
	    }

	    public void setUpdatedAt(LocalDateTime updatedAt) {
	        this.updatedAt = updatedAt;
	    }

	  


	    @PrePersist
	    protected void onCreate() {
	        this.createdAt = LocalDateTime.now();
	               
	    }

	    @PreUpdate
	    protected void onUpdate() {
	        this.updatedAt = LocalDateTime.now();
	       
	    }
	    

}

