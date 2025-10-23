package com.striveconnect.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity for the `courses` table.
 * This table now holds only the core, non-translatable course data.
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
}

