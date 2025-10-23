package com.striveconnect.service;

import com.striveconnect.dto.EmploymentHistoryDto;
import com.striveconnect.entity.EmploymentHistory;
import com.striveconnect.entity.User;
import com.striveconnect.repository.EmploymentHistoryRepository;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmploymentHistoryService {

    private final EmploymentHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public EmploymentHistoryService(EmploymentHistoryRepository historyRepository, UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    // --- ALUMNI METHODS ---

    @Transactional
    public EmploymentHistoryDto addEmploymentHistory(EmploymentHistoryDto historyDto) {
        User currentUser = getCurrentUser();
        
        // --- "SMART UPDATE" LOGIC ---
        if (historyDto.getEndDate() == null) { // If this is a new "Present" job
            Optional<EmploymentHistory> existingPresentJob = historyRepository.findByUserAndEndDateIsNull(currentUser);
            
            if (existingPresentJob.isPresent()) {
                EmploymentHistory oldJob = existingPresentJob.get();
                if (historyDto.getStartDate().isBefore(oldJob.getStartDate())) {
                     throw new ResponseStatusException(HttpStatus.CONFLICT, "New job start date cannot be before current job start date.");
                }
                oldJob.setEndDate(historyDto.getStartDate().minusDays(1));
                historyRepository.save(oldJob);
            }
        }
        
        validateOverlaps(currentUser, historyDto, null);

        EmploymentHistory newHistory = new EmploymentHistory();
        newHistory.setUser(currentUser);
        newHistory.setCompanyName(historyDto.getCompanyName());
        newHistory.setJobTitle(historyDto.getJobTitle());
        newHistory.setStartDate(historyDto.getStartDate());
        newHistory.setEndDate(historyDto.getEndDate());
        newHistory.setLocation(historyDto.getLocation());
        newHistory.setStatus(EmploymentHistory.VerificationStatus.PENDING_VERIFICATION);

        EmploymentHistory savedHistory = historyRepository.save(newHistory);

        // --- DATA SYNC TO PROFILE ---
        if (savedHistory.getEndDate() == null) {
            currentUser.setCurrentCompany(savedHistory.getCompanyName());
            currentUser.setCurrentCity(savedHistory.getLocation());
            userRepository.save(currentUser);
        }

        return convertToDto(savedHistory);
    }

    @Transactional
    public EmploymentHistoryDto updateEmploymentHistory(Long historyId, EmploymentHistoryDto historyDto) {
        User currentUser = getCurrentUser();
        EmploymentHistory record = historyRepository.findById(historyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employment record not found."));

        if (!record.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to edit this record.");
        }

        validateOverlaps(currentUser, historyDto, historyId);

        boolean wasPresentJob = record.getEndDate() == null;
        boolean isNowPresentJob = historyDto.getEndDate() == null;

        if (isNowPresentJob && !wasPresentJob) {
            Optional<EmploymentHistory> existingPresentJob = historyRepository.findByUserAndEndDateIsNull(currentUser);
            if (existingPresentJob.isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "You already have a job marked as 'Present'. Please update that entry first.");
            }
        }

        if (record.getStatus() == EmploymentHistory.VerificationStatus.VERIFIED) {
            record.setEndDate(historyDto.getEndDate());
        } else {
            record.setCompanyName(historyDto.getCompanyName());
            record.setJobTitle(historyDto.getJobTitle());
            record.setStartDate(historyDto.getStartDate());
            record.setEndDate(historyDto.getEndDate());
            record.setLocation(historyDto.getLocation());
        }
        
        if (record.getStatus() == EmploymentHistory.VerificationStatus.VERIFIED && !wasPresentJob) {
             record.setStatus(EmploymentHistory.VerificationStatus.PENDING_VERIFICATION);
             record.setVerifiedBy(null);
        }

        EmploymentHistory savedHistory = historyRepository.save(record);

        if (savedHistory.getEndDate() == null) {
            currentUser.setCurrentCompany(savedHistory.getCompanyName());
            currentUser.setCurrentCity(savedHistory.getLocation());
        } else if (wasPresentJob && savedHistory.getEndDate() != null) {
            if (currentUser.getCurrentCompany() != null && currentUser.getCurrentCompany().equals(record.getCompanyName())) {
                 currentUser.setCurrentCompany(null);
                 currentUser.setCurrentCity(null);
            }
        }
        userRepository.save(currentUser);

        return convertToDto(savedHistory);
    }

    public List<EmploymentHistoryDto> getMyEmploymentHistory() {
        User currentUser = getCurrentUser();
        return getEmploymentHistoryForUser(currentUser);
    }
    
    // --- ADMIN METHODS ---
    public List<EmploymentHistoryDto> getPendingHistory() {
        String tenantId = TenantContext.getCurrentTenant();
        List<EmploymentHistory> historyList = historyRepository.findByTenantIdAndStatus(tenantId, EmploymentHistory.VerificationStatus.PENDING_VERIFICATION);
        return historyList.stream()
                .map(this::convertToDtoWithAuthor)
                .collect(Collectors.toList());
    }

    public List<EmploymentHistoryDto> getHistoryForUser(String userId) {
        User adminUser = getCurrentUser();
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target user not found."));

        if (!adminUser.getTenantId().equals(targetUser.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access to this user's history is denied.");
        }

        return getEmploymentHistoryForUser(targetUser);
    }
    
    public void verifyEmploymentRecord(Long historyId) {
        User adminUser = getCurrentUser();
        EmploymentHistory record = historyRepository.findById(historyId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employment record not found."));

        if (!record.getUser().getTenantId().equals(adminUser.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access to this record is denied.");
        }

        record.setStatus(EmploymentHistory.VerificationStatus.VERIFIED);
        record.setVerifiedBy(adminUser);
        historyRepository.save(record);
    }

    // --- PRIVATE HELPERS ---
    private void validateOverlaps(User user, EmploymentHistoryDto dto, Long currentHistoryId) {
        List<EmploymentHistory> allHistory = historyRepository.findByUser(user);
        LocalDate newStart = dto.getStartDate();
        if (newStart == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be empty.");
        LocalDate newEnd = dto.getEndDate() == null ? LocalDate.MAX : dto.getEndDate();

        if (newStart.isAfter(newEnd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be after end date.");
        }

        for (EmploymentHistory record : allHistory) {
            if (currentHistoryId != null && record.getEmploymentId().equals(currentHistoryId)) continue;
            LocalDate oldStart = record.getStartDate();
            LocalDate oldEnd = record.getEndDate() == null ? LocalDate.MAX : record.getEndDate();
            if (newStart.isBefore(oldEnd) && newEnd.isAfter(oldStart)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "The dates for this job overlap with an existing entry.");
            }
        }
    }
    
    private List<EmploymentHistoryDto> getEmploymentHistoryForUser(User user) {
        List<EmploymentHistory> historyList = historyRepository.findByUser(user);
        return historyList.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    private User getCurrentUser() {
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authenticated user not found."));
    }

    private EmploymentHistoryDto convertToDtoWithAuthor(EmploymentHistory history) {
        EmploymentHistoryDto dto = convertToDto(history);
        if (history.getUser() != null) {
            dto.setAuthorName(history.getUser().getFullName());
        }
        return dto;
    }
    
    private EmploymentHistoryDto convertToDto(EmploymentHistory history) {
        EmploymentHistoryDto dto = new EmploymentHistoryDto();
        dto.setEmploymentId(history.getEmploymentId());
        dto.setCompanyName(history.getCompanyName());
        dto.setJobTitle(history.getJobTitle());
        dto.setStartDate(history.getStartDate());
        dto.setEndDate(history.getEndDate());
        dto.setLocation(history.getLocation());
        dto.setStatus(history.getStatus().name());
        return dto;
    }
}

