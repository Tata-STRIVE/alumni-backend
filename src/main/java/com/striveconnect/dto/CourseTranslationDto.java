package com.striveconnect.dto;

import com.striveconnect.entity.CourseTranslation;

public class CourseTranslationDto {

    private String languageCode;
    private String name;
    private String description;
    private String eligibilityCriteria;
    private String careerPath;

    public static CourseTranslationDto fromEntity(CourseTranslation t) {
        CourseTranslationDto dto = new CourseTranslationDto();
        dto.setLanguageCode(t.getLanguageCode());
        dto.setName(t.getName());
        dto.setDescription(t.getDescription());
        dto.setEligibilityCriteria(t.getEligibilityCriteria());
        dto.setCareerPath(t.getCareerPath());
        return dto;
    }

    // Getters & Setters
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
