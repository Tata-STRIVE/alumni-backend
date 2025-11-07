package com.striveconnect.controller;

import com.striveconnect.dto.ApplicationDto;
import com.striveconnect.dto.CreateUpskillingOpportunityDto;
import com.striveconnect.dto.UpdateApplicationStatusDto;
import com.striveconnect.dto.UpskillingApplicationDto;
import com.striveconnect.dto.UpskillingOpportunityDto;
import com.striveconnect.service.UpskillingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upskilling")
public class UpskillingController {

    private final UpskillingService upskillingService;

    public UpskillingController(UpskillingService upskillingService) {
        this.upskillingService = upskillingService;
    }

    // --- ANY AUTHENTICATED USER ---
    @GetMapping("/tenantId/{tenantId}")
    public ResponseEntity<List<UpskillingOpportunityDto>> getOpportunities() {
        List<UpskillingOpportunityDto> opportunities = upskillingService.getOpportunitiesForCurrentTenant();
        return ResponseEntity.ok(opportunities);
    }
    
    // NEW: Endpoint for an alumnus to get their upskilling applications
    @GetMapping("/my-applications")
    public ResponseEntity<List<UpskillingApplicationDto>> getMyApplications() {
        List<UpskillingApplicationDto> applications = upskillingService.getMyUpskillingApplications();
        return ResponseEntity.ok(applications);
    }

    // --- ALUMNUS-ONLY ENDPOINTS ---
    @PostMapping("/{opportunityId}/apply")
    public ResponseEntity<?> applyToOpportunity(@PathVariable Long opportunityId) {
        upskillingService.applyToOpportunity(opportunityId);
        return ResponseEntity.ok(Map.of("message", "Application for upskilling submitted successfully"));
    }

    // --- ADMIN-ONLY ENDPOINTS ---
    @PostMapping
    public ResponseEntity<UpskillingOpportunityDto> createOpportunity(@RequestBody CreateUpskillingOpportunityDto createDto) {
        UpskillingOpportunityDto createdOpportunity = upskillingService.createOpportunity(createDto);
        return new ResponseEntity<>(createdOpportunity, HttpStatus.CREATED);
    }

    @GetMapping("/{opportunityId}/applications")
    public ResponseEntity<List<ApplicationDto>> getOpportunityApplications(@PathVariable Long opportunityId) {
        List<ApplicationDto> applications = upskillingService.getApplicationsForOpportunity(opportunityId);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody UpdateApplicationStatusDto statusDto) {
        upskillingService.updateApplicationStatus(applicationId, statusDto);
        return ResponseEntity.ok(Map.of("message", "Upskilling application status updated successfully"));
    }
    
    @PutMapping("/{opportunityId}")
    public ResponseEntity<UpskillingOpportunityDto> updateOpportunity(
            @PathVariable Long opportunityId,
            @RequestBody CreateUpskillingOpportunityDto updateDto) {

        UpskillingOpportunityDto updated = upskillingService.updateOpportunity(opportunityId, updateDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{opportunityId}")
    public ResponseEntity<?> deleteOpportunity(@PathVariable Long opportunityId) {
        upskillingService.deleteOpportunity(opportunityId);
        return ResponseEntity.ok(Map.of("message", "Opportunity deleted successfully"));
    }

}

