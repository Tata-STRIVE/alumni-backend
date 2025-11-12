package com.striveconnect.dto;

import com.striveconnect.entity.Batch;
import com.striveconnect.entity.CourseTranslation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class BatchDto {
    private Long batchId;
    private String batchName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long courseId;
    private Long centerId;
    private String courseName;
    private String centerName;
    private String centerCity;
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active = true;

    // getters/setters
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getCenterId() { return centerId; }
    public void setCenterId(Long centerId) { this.centerId = centerId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCenterName() { return centerName; }
    public void setCenterName(String centerName) { this.centerName = centerName; }
    public String getCenterCity() { return centerCity; }
    public void setCenterCity(String centerCity) { this.centerCity = centerCity; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    /**
     * Convert Batch entity -> DTO selecting translation by languageCode (falls back to EN).
     */
    public static BatchDto fromEntity(Batch b, String languageCode) {
        if (b == null) return null;
        BatchDto dto = new BatchDto();
        dto.setBatchId(b.getBatchId());
        dto.setBatchName(b.getBatchName());
        dto.setStartDate(b.getStartDate());
        dto.setEndDate(b.getEndDate());
        dto.setStatus(b.getStatus() != null ? b.getStatus().name() : null);
        dto.setTenantId(b.getTenantId());
        dto.setActive(b.isActive());
        if (b.getCourse() != null) {
            dto.setCourseId(b.getCourse().getCourseId());
            // pick translation
            if (b.getCourse().getTranslations() != null) {
                CourseTranslation chosen = b.getCourse().getTranslations().stream()
                        .filter(t -> t.getLanguageCode().equalsIgnoreCase(languageCode))
                        .findFirst()
                        .orElseGet(() -> b.getCourse().getTranslations().stream()
                                .filter(t -> t.getLanguageCode().equalsIgnoreCase("EN"))
                                .findFirst()
                                .orElse(null));
                if (chosen != null) dto.setCourseName(chosen.getName());
            }
        }
        if (b.getCenter() != null) {
            dto.setCenterId(b.getCenter().getCenterId());
            dto.setCenterName(b.getCenter().getName());
            dto.setCenterCity(b.getCenter().getCity());
        }
        return dto;
    }
}
