package com.striveconnect.entity;

import jakarta.persistence.*;

/**
 * NEW Entity for the `course_translations` table.
 * Holds all multilingual content for a course.
 */
@Entity
@Table(name = "course_translations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"course_id", "language_code"})
})
public class CourseTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long translationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String languageCode; // 'en', 'hi', 'te'

    @Column(nullable = false)
    private String name;
    
    @Lob
    private String description;
    
    @Lob
    private String eligibilityCriteria;
    
    @Lob
    private String careerPath;

    // Getters and Setters
    public Long getTranslationId() { return translationId; }
    public void setTranslationId(Long translationId) { this.translationId = translationId; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEligibilityCriteria() { return eligibilityCriteria; }
    public void setEligibilityCriteria(String eligibilityCriteria) { this.eligibilityCriteria = eligibilityCriteria; }
    public String getCareerPath() { return careerPath; }
    public void setCareerPath(String careerPath) { this.careerPath = careerPath; }
}
