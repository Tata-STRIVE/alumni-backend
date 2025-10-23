package com.striveconnect.controller;

import com.striveconnect.dto.FeedbackDto;
import com.striveconnect.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ALUMNUS')")
    public ResponseEntity<FeedbackDto> submitFeedback(@RequestBody FeedbackDto feedbackDto) {
        FeedbackDto savedFeedback = feedbackService.submitFeedback(feedbackDto);
        return new ResponseEntity<>(savedFeedback, HttpStatus.CREATED);
    }

    @GetMapping("/{type}/{relatedId}")
    @PreAuthorize("hasAnyAuthority('CENTER_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<FeedbackDto>> getFeedback(
            @PathVariable String type,
            @PathVariable Long relatedId) {
        List<FeedbackDto> feedbackList = feedbackService.getFeedbackForItem(type, relatedId);
        return ResponseEntity.ok(feedbackList);
    }
}
