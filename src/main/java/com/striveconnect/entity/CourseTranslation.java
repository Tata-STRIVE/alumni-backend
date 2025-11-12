package com.striveconnect.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "course_translations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "language_code"}))
public class CourseTranslation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long translationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String languageCode; // EN, HI, TE

    @Column(nullable = false)
    private String name;

    @Lob
    private String description;

    @Lob
    private String eligibilityCriteria;

    @Lob
    private String careerPath;

    // getters / setters
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
