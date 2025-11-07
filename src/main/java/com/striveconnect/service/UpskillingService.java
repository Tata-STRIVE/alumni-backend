package com.striveconnect.service;

import com.striveconnect.dto.ApplicationDto;
import com.striveconnect.dto.CreateUpskillingOpportunityDto;
import com.striveconnect.dto.UpdateApplicationStatusDto;
import com.striveconnect.dto.UpskillingApplicationDto;
import com.striveconnect.dto.UpskillingOpportunityDto;
import com.striveconnect.entity.UpskillingApplication;
import com.striveconnect.entity.UpskillingOpportunity;
import com.striveconnect.entity.User;
import com.striveconnect.repository.UpskillingApplicationRepository;
import com.striveconnect.repository.UpskillingOpportunityRepository;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpskillingService {

    private final UpskillingOpportunityRepository opportunityRepository;
    private final UpskillingApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public UpskillingService(UpskillingOpportunityRepository opportunityRepository, UpskillingApplicationRepository applicationRepository, UserRepository userRepository) {
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets all upskilling opportunities for the current tenant.
     */
    public List<UpskillingOpportunityDto> getOpportunitiesForCurrentTenant() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set.");
        }
        List<UpskillingOpportunity> opportunities = opportunityRepository.findByTenantId(tenantId);
        return opportunities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
  
    
    /**
     * NEW: Gets all upskilling applications for the currently logged-in alumnus.
     */
    public List<UpskillingApplicationDto> getMyUpskillingApplications() {
        User currentUser = getCurrentUser();
        List<UpskillingApplication> applications = applicationRepository.findByUser(currentUser);
        return applications.stream()
                .map(this::convertUpskillingApplicationToDto)
                .collect(Collectors.toList());
    }
    
    
    
    /**
     * NEW: Helper method to get the currently authenticated user.
     */
    private User getCurrentUser() {
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authenticated user not found."));
    }
    
    /**
     * NEW: DTO converter for an upskilling application.
     */
    private UpskillingApplicationDto convertUpskillingApplicationToDto(UpskillingApplication app) {
        UpskillingApplicationDto dto = new UpskillingApplicationDto();
        dto.setApplicationId(app.getApplicationId());
        dto.setStatus(app.getStatus().name());
        dto.setAppliedAt(app.getAppliedAt());
        
        UpskillingOpportunity opportunity = app.getOpportunity();
        if (opportunity != null) {
            dto.setOpportunityId(opportunity.getOpportunityId());
            dto.setOpportunityTitle(opportunity.getTitle());
        }
        return dto;
    }
    /**
     * Admin: Creates a new upskilling opportunity.
     */
    public UpskillingOpportunityDto createOpportunity(CreateUpskillingOpportunityDto createDto) {
        String tenantId = TenantContext.getCurrentTenant();
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        User adminUser = userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found"));

        UpskillingOpportunity opportunity = new UpskillingOpportunity();
        opportunity.setTenantId(tenantId);
        opportunity.setTitle(createDto.getTitle());
        opportunity.setDescription(createDto.getDescription());
        opportunity.setPrerequisites(createDto.getPrerequisites());
        opportunity.setStartDate(createDto.getStartDate());
        opportunity.setPostedBy(adminUser);
        opportunity.setCreatedAt(LocalDateTime.now());

        UpskillingOpportunity savedOpportunity = opportunityRepository.save(opportunity);
        return convertToDto(savedOpportunity);
    }

    /**
     * Admin: Gets all applications for a specific opportunity.
     */
    public List<ApplicationDto> getApplicationsForOpportunity(Long opportunityId) {
        UpskillingOpportunity opportunity = findOpportunityAndVerifyTenant(opportunityId);
        List<UpskillingApplication> applications = applicationRepository.findByOpportunityIdAndStatus(opportunityId);

        return applications.stream()
                .map(this::convertApplicationToDto)
                .collect(Collectors.toList());
    }

    /**
     * Admin: Updates the status of an upskilling application.
     */
    public void updateApplicationStatus(Long applicationId, UpdateApplicationStatusDto statusDto) {
        UpskillingApplication application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        // Verify that the opportunity associated with this application belongs to the admin's tenant
        findOpportunityAndVerifyTenant(application.getOpportunity().getOpportunityId());

        try {
            UpskillingApplication.ApplicationStatus newStatus = UpskillingApplication.ApplicationStatus.valueOf(statusDto.getStatus().toUpperCase());
         System.out.println(newStatus);
            
            application.setStatus(newStatus);
            System.out.println(newStatus);

            
            applicationRepository.save(application);
            
            System.out.println(newStatus);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status provided");
        }
    }

    // Helper to find an opportunity and verify tenant access
    private UpskillingOpportunity findOpportunityAndVerifyTenant(Long opportunityId) {
        String tenantId = TenantContext.getCurrentTenant();
        UpskillingOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found"));

        if (!opportunity.getTenantId().equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access to this resource is denied");
        }
        return opportunity;
    }

    // --- DTO Converters ---
    private UpskillingOpportunityDto convertToDto(UpskillingOpportunity opportunity) {
        UpskillingOpportunityDto dto = new UpskillingOpportunityDto();
        dto.setOpportunityId(opportunity.getOpportunityId());
        dto.setTitle(opportunity.getTitle());
        dto.setDescription(opportunity.getDescription());
        dto.setPrerequisites(opportunity.getPrerequisites());
        dto.setStartDate(opportunity.getStartDate());
        return dto;
    }
    
    private ApplicationDto convertApplicationToDto(UpskillingApplication application) {
        ApplicationDto dto = new ApplicationDto();
        dto.setApplicationId(application.getApplicationId());
        dto.setApplicantName(application.getUser().getFullName());
        dto.setApplicantMobile(application.getUser().getMobileNumber());
        dto.setStatus(application.getStatus().name());
        return dto;
    }
    
    /**
     * Handles the logic for an alumnus applying to an upskilling opportunity.
     */
    public void applyToOpportunity(Long opportunityId) {
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        User currentUser = userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UpskillingOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Opportunity not found"));

        if (!opportunity.getTenantId().equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access to this opportunity is denied");
        }

        if (applicationRepository.existsByOpportunityAndUser(opportunity, currentUser)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already applied for this opportunity");
        }

        UpskillingApplication application = new UpskillingApplication();
        application.setOpportunity(opportunity);
        application.setUser(currentUser);
        application.setStatus(UpskillingApplication.ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());
        
        applicationRepository.save(application);
    }
    
    
    @Transactional
    public UpskillingOpportunityDto updateOpportunity(Long id, CreateUpskillingOpportunityDto dto) {
        UpskillingOpportunity opportunity = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Upskilling opportunity not found"));

        opportunity.setTitle(dto.getTitle());
        opportunity.setDescription(dto.getDescription());
        opportunity.setStartDate(dto.getStartDate());
        opportunity.setEndDate(dto.getEndDate());
       // opportunity.setLink(dto.getLink());

        UpskillingOpportunity saved = opportunityRepository.save(opportunity);
        return UpskillingOpportunityDto.fromEntity(saved);
    }
    
    @Transactional
    public void deleteOpportunity(Long id) {
        UpskillingOpportunity opportunity = opportunityRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Upskilling opportunity not found"));
        opportunityRepository.delete(opportunity);
    }

    
}

