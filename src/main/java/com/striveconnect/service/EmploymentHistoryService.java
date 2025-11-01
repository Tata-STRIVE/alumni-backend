package com.striveconnect.service;

import com.striveconnect.dto.AdminReviewDto;
import com.striveconnect.dto.EmploymentHistoryDto;
import com.striveconnect.entity.EmploymentHistory;
import com.striveconnect.entity.EmploymentHistory.VerificationStatus;
import com.striveconnect.entity.User;
import com.striveconnect.repository.EmploymentHistoryRepository;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmploymentHistoryService {

    private final EmploymentHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public EmploymentHistoryService(EmploymentHistoryRepository historyRepository, UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    /**
     * Alumnus: Gets their own history.
     */
    public List<EmploymentHistoryDto> getMyHistory() {
        User currentUser = getCurrentUser();
        return historyRepository.findByAuthorOrderByStartDateDesc(currentUser)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Alumnus: Adds a new history record.
     */
    public EmploymentHistoryDto addHistory(EmploymentHistoryDto dto) {
        User currentUser = getCurrentUser();
        
        // --- "SMART UPDATE" LOGIC ---
        if (dto.getEndDate() == null) { // If this is a new "Present" job
            Optional<EmploymentHistory> existingPresentJob = historyRepository.findByAuthorAndEndDateIsNull(currentUser);
            
            if (existingPresentJob.isPresent()) {
                // Auto-close the old "Present" job
                EmploymentHistory oldJob = existingPresentJob.get();
                if (dto.getStartDate().isBefore(oldJob.getStartDate())) {
                     throw new ResponseStatusException(HttpStatus.CONFLICT, "New job start date cannot be before current job start date.");
                }
                oldJob.setEndDate(dto.getStartDate().minusDays(1));
                historyRepository.save(oldJob);
            }
        }
        
        EmploymentHistory history = new EmploymentHistory();
        history.setAuthor(currentUser);
        history.setStatus(VerificationStatus.PENDING_VERIFICATION);
        
        // Map all fields from DTO
        updateEntityFromDto(history, dto);
        
        EmploymentHistory saved = historyRepository.save(history);
        
        // --- DATA SYNC TO PROFILE ---
        if (saved.getEndDate() == null) {
            currentUser.setCurrentCompany(saved.getCompanyName());
            currentUser.setCurrentCity(saved.getLocation());
            userRepository.save(currentUser);
        }

        return convertToDto(saved);
    }

    /**
     * Alumnus: Updates a rejected or pending record.
     */
    public EmploymentHistoryDto updateHistory(Long historyId, EmploymentHistoryDto dto) {
        User currentUser = getCurrentUser();
        EmploymentHistory history = historyRepository.findById(historyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History record not found."));

        // Security check
        if (!history.getAuthor().getUserId().equals(currentUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this record.");
        }

        // Business logic check
        if (history.getStatus() == VerificationStatus.VERIFIED) {
             // Allow editing only end date for a verified record
            history.setEndDate(dto.getEndDate());
             // If end date was added, it may need re-verification
            if(dto.getEndDate() != null) {
                 history.setStatus(VerificationStatus.PENDING_VERIFICATION);
            }
        } else {
            // Allow full edit for PENDING or REJECTED
            updateEntityFromDto(history, dto);
            history.setStatus(VerificationStatus.PENDING_VERIFICATION);
            history.setAdminRemarks(null); // Clear old rejection remarks
        }
        
        EmploymentHistory saved = historyRepository.save(history);
        return convertToDto(saved);
    }

    /**
     * Admin: Gets all pending records for their tenant.
     */
    public List<EmploymentHistoryDto> getPendingHistory() {
        String tenantId = TenantContext.getCurrentTenant();
        return historyRepository.findByTenantAndStatus(tenantId, VerificationStatus.PENDING_VERIFICATION)
                .stream()
                .map(this::convertToDto) // DTO converter now includes authorName
                .collect(Collectors.toList());
    }

    /**
     * Admin: Gets history for a specific user.
     */
    public List<EmploymentHistoryDto> getHistoryForUser(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));
        return historyRepository.findByAuthorOrderByStartDateDesc(user)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Admin: Reviews (Approves/Rejects) a pending record.
     */
    public EmploymentHistoryDto reviewHistory(Long historyId, AdminReviewDto reviewDto) {
        // Admin user is validated by security config
        EmploymentHistory history = historyRepository.findById(historyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "History record not found."));

        if (reviewDto.getStatus().equalsIgnoreCase("VERIFIED")) {
            history.setStatus(VerificationStatus.VERIFIED);
            history.setAdminRemarks(null);
        } else if (reviewDto.getStatus().equalsIgnoreCase("REJECTED")) {
            if (reviewDto.getRemarks() == null || reviewDto.getRemarks().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rejection remarks are required.");
            }
            history.setStatus(VerificationStatus.REJECTED);
            history.setAdminRemarks(reviewDto.getRemarks());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status provided.");
        }

        EmploymentHistory saved = historyRepository.save(history);
        return convertToDto(saved);
    }

    // --- Helper Methods ---

    private User getCurrentUser() {
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authenticated user not found."));
    }
    
    // Helper to map DTO fields to the Entity
    private void updateEntityFromDto(EmploymentHistory history, EmploymentHistoryDto dto) {
        history.setCompanyName(dto.getCompanyName());
        history.setJobTitle(dto.getJobTitle());
        history.setLocation(dto.getLocation());
        history.setStartDate(dto.getStartDate());
        history.setEndDate(dto.getEndDate());
        history.setAttachmentType(dto.getAttachmentType());
        history.setAttachmentUrl(dto.getAttachmentUrl());
    }

    // Helper to map Entity fields to the DTO
    private EmploymentHistoryDto convertToDto(EmploymentHistory history) {
        EmploymentHistoryDto dto = new EmploymentHistoryDto();
        dto.setEmploymentId(history.getEmploymentId());
        dto.setCompanyName(history.getCompanyName());
        dto.setJobTitle(history.getJobTitle());
        dto.setLocation(history.getLocation());
        dto.setStartDate(history.getStartDate());
        dto.setEndDate(history.getEndDate());
        dto.setStatus(history.getStatus().name());
        dto.setAuthorName(history.getAuthor().getFullName()); // Include author name
        dto.setAttachmentType(history.getAttachmentType());
        dto.setAttachmentUrl(history.getAttachmentUrl());
        dto.setAdminRemarks(history.getAdminRemarks());
        return dto;
    }
}

