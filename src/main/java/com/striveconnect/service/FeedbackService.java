package com.striveconnect.service;

import com.striveconnect.dto.FeedbackDto;
import com.striveconnect.entity.Feedback;
import com.striveconnect.entity.User;
import com.striveconnect.repository.FeedbackRepository;
import com.striveconnect.repository.JobPostingRepository;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository; // To validate job IDs

    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository, JobPostingRepository jobPostingRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.jobPostingRepository = jobPostingRepository;
    }

    /**
     * Allows an authenticated alumnus to submit feedback.
     */
    public FeedbackDto submitFeedback(FeedbackDto feedbackDto) {
        User currentUser = getCurrentUser();
        String tenantId = currentUser.getTenantId();

        Feedback.FeedbackType type;
        try {
            type = Feedback.FeedbackType.valueOf(feedbackDto.getFeedbackType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid feedback type.");
        }
        
        // Validate that the related entity (job, course etc.) exists and belongs to the tenant
        validateRelatedEntity(type, feedbackDto.getRelatedId(), tenantId);

        Feedback feedback = new Feedback();
        feedback.setTenantId(tenantId);
        feedback.setUser(currentUser);
        feedback.setFeedbackType(type);
        feedback.setRelatedId(feedbackDto.getRelatedId());
        feedback.setRating(feedbackDto.getRating());
        feedback.setComment(feedbackDto.getComment());
        feedback.setCreatedAt(LocalDateTime.now());
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return convertToDto(savedFeedback);
    }

    /**
     * Admin: Retrieves all feedback for a specific item (e.g., a course).
     */
    public List<FeedbackDto> getFeedbackForItem(String type, Long relatedId) {
        String tenantId = TenantContext.getCurrentTenant();
        Feedback.FeedbackType feedbackType = Feedback.FeedbackType.valueOf(type.toUpperCase());
        
        List<Feedback> feedbackList = feedbackRepository.findByTenantIdAndFeedbackTypeAndRelatedId(tenantId, feedbackType, relatedId);
        return feedbackList.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private void validateRelatedEntity(Feedback.FeedbackType type, Long relatedId, String tenantId) {
        if (type == Feedback.FeedbackType.JOB) {
            jobPostingRepository.findById(relatedId)
                .filter(job -> job.getTenantId().equals(tenantId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job posting not found in this tenant."));
        }
        // Add validation for COURSE type if a CourseRepository exists
    }

    private User getCurrentUser() {
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authenticated user not found."));
    }

    private FeedbackDto convertToDto(Feedback feedback) {
        FeedbackDto dto = new FeedbackDto();
        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setFeedbackType(feedback.getFeedbackType().name());
        dto.setRelatedId(feedback.getRelatedId());
        dto.setRating(feedback.getRating());
        dto.setComment(feedback.getComment());
        dto.setAuthorName(feedback.getUser().getFullName());
        return dto;
    }
}
