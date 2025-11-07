package com.striveconnect.dto;

import com.striveconnect.entity.UpskillingOpportunity;
import java.time.LocalDate;

public class UpskillingOpportunityDto {
    private Long opportunityId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String link;

    // --- Getters & Setters ---
    public Long getOpportunityId() { return opportunityId; }
    public void setOpportunityId(Long opportunityId) { this.opportunityId = opportunityId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    // ✅ Static factory method for entity → DTO conversion
    public static UpskillingOpportunityDto fromEntity(UpskillingOpportunity entity) {
        UpskillingOpportunityDto dto = new UpskillingOpportunityDto();
        dto.setOpportunityId(entity.getOpportunityId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setLink(entity.getLink());
        return dto;
    }
	public void setPrerequisites(String prerequisites) {
		// TODO Auto-generated method stub
		
	}
}
