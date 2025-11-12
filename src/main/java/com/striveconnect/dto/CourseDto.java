package com.striveconnect.dto;

import com.striveconnect.entity.Course;
import com.striveconnect.entity.CourseTranslation;

import java.util.ArrayList;
import java.util.List;

public class CourseDto {

    private Long courseId;
    private String tenantId;
    private String iconUrl;
    private boolean active;

    // Display-friendly fields (used by UI for specific language)
    private String name;
    private String description;
    private String eligibilityCriteria;
    private String careerPath;

    private List<TranslationDto> translations = new ArrayList<>();

    // ----------------------------------------------------------
    // ✅ Getters and Setters
    // ----------------------------------------------------------

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

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

    public String getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    public void setEligibilityCriteria(String eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }

    public String getCareerPath() {
        return careerPath;
    }

    public void setCareerPath(String careerPath) {
        this.careerPath = careerPath;
    }

    public List<TranslationDto> getTranslations() {
        return translations;
    }

    public void setTranslations(List<TranslationDto> translations) {
        this.translations = translations;
    }

    // ----------------------------------------------------------
    // ✅ Nested Translation DTO
    // ----------------------------------------------------------
    public static class TranslationDto {
        private String languageCode;
        private String name;
        private String description;
        private String eligibilityCriteria;
        private String careerPath;

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }

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

        public String getEligibilityCriteria() {
            return eligibilityCriteria;
        }

        public void setEligibilityCriteria(String eligibilityCriteria) {
            this.eligibilityCriteria = eligibilityCriteria;
        }

        public String getCareerPath() {
            return careerPath;
        }

        public void setCareerPath(String careerPath) {
            this.careerPath = careerPath;
        }

        public static TranslationDto fromEntity(CourseTranslation t) {
            TranslationDto dto = new TranslationDto();
            dto.setLanguageCode(t.getLanguageCode());
            dto.setName(t.getName());
            dto.setDescription(t.getDescription());
            dto.setEligibilityCriteria(t.getEligibilityCriteria());
            dto.setCareerPath(t.getCareerPath());
            return dto;
        }
    }
}
