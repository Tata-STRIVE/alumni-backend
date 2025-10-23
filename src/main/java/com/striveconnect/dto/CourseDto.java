package com.striveconnect.dto;

import java.util.List;

/**
 * Represents a single course, translated into the requested language.
 * This DTO will be displayed on the public "Courses" page.
 */
public class CourseDto {

    private Long courseId;
    private String iconUrl;
    
    // Translated fields
    private String name;
    private String description;
    private String eligibilityCriteria;
    private String careerPath;

    // Upcoming batches for this course
    private List<BatchDto> upcomingBatches;

    // Getters and Setters
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEligibilityCriteria() { return eligibilityCriteria; }
    public void setEligibilityCriteria(String eligibilityCriteria) { this.eligibilityCriteria = eligibilityCriteria; }
    public String getCareerPath() { return careerPath; }
    public void setCareerPath(String careerPath) { this.careerPath = careerPath; }
    public List<BatchDto> getUpcomingBatches() { return upcomingBatches; }
    public void setUpcomingBatches(List<BatchDto> upcomingBatches) { this.upcomingBatches = upcomingBatches; }
}

