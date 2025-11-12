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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmploymentHistoryService {

    private final EmploymentHistoryRepository employmentHistoryRepository;
    private final UserRepository userRepository;

    public EmploymentHistoryService(EmploymentHistoryRepository employmentHistoryRepository,
                                    UserRepository userRepository) {
        this.employmentHistoryRepository = employmentHistoryRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenant = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobile, tenant)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @Transactional
    public EmploymentHistoryDto createEmploymentHistory(EmploymentHistoryDto dto) {
        User user = getCurrentUser();

        EmploymentHistory e = new EmploymentHistory();
        e.setAuthor(user);
        e.setCompanyName(dto.getCompanyName());
        e.setJobTitle(dto.getJobTitle());
        e.setLocation(dto.getLocation());
        e.setStartDate(dto.getStartDate());
        e.setEndDate(dto.getEndDate());
        e.setEngagementId(dto.getEngagementId());
        e.setAttachmentType(dto.getAttachmentType());
        e.setAttachmentUrl(dto.getAttachmentUrl());
        e.setTenantId(user.getTenantId());
        e.setStatus(EmploymentHistory.VerificationStatus.PENDING_VERIFICATION);
        e.setCreatedAt(LocalDateTime.now());
        e.setUpdatedAt(LocalDateTime.now());

        employmentHistoryRepository.save(e);
        return EmploymentHistoryDto.fromEntity(e);
    }

    @Transactional
    public EmploymentHistoryDto updateEmploymentHistory(Long id, EmploymentHistoryDto dto) {
        EmploymentHistory e = employmentHistoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employment record not found"));

        e.setCompanyName(dto.getCompanyName());
        e.setJobTitle(dto.getJobTitle());
        e.setLocation(dto.getLocation());
        e.setEndDate(dto.getEndDate());
        e.setAttachmentType(dto.getAttachmentType());
        e.setAttachmentUrl(dto.getAttachmentUrl());
        e.setUpdatedAt(LocalDateTime.now());

        return EmploymentHistoryDto.fromEntity(employmentHistoryRepository.save(e));
    }

    @Transactional(readOnly = true)
    public List<EmploymentHistoryDto> getEmploymentHistoryForCurrentUser() {
        User user = getCurrentUser();
        return employmentHistoryRepository.findByAuthor(user).stream()
                .map(EmploymentHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmploymentHistoryDto> getPendingEmploymentHistory() {
        return employmentHistoryRepository.findByStatus(EmploymentHistory.VerificationStatus.PENDING_VERIFICATION).stream()
                .map(EmploymentHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public void verifyEmploymentHistory(Long id, boolean approved, String remarks) {
        EmploymentHistory e = employmentHistoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employment record not found"));
        e.setStatus(approved ? EmploymentHistory.VerificationStatus.VERIFIED
                             : EmploymentHistory.VerificationStatus.REJECTED);
        e.setAdminRemarks(remarks);
        e.setUpdatedAt(LocalDateTime.now());
        employmentHistoryRepository.save(e);
    }
    
    
    
    /**
     * Admin: Reviews (Approves/Rejects) a pending record.
     */
    public EmploymentHistoryDto reviewHistory(Long historyId, AdminReviewDto reviewDto) {
        // Admin user is validated by security config
        EmploymentHistory history = employmentHistoryRepository.findById(historyId)
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

        EmploymentHistory saved = employmentHistoryRepository.save(history);
        return convertToDto(saved);
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
        dto.setStatus(history.getStatus());
      //  dto.setAuthorName(history.getAuthor().getFullName()); // Include author name
        dto.setAttachmentType(history.getAttachmentType());
        dto.setAttachmentUrl(history.getAttachmentUrl());
        dto.setAdminRemarks(history.getAdminRemarks());
        return dto;
    }
}
