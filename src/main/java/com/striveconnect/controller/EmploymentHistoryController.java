package com.striveconnect.controller;

import com.striveconnect.dto.AdminReviewDto;
import com.striveconnect.dto.EmploymentHistoryDto;
import com.striveconnect.service.EmploymentHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employment-history")
public class EmploymentHistoryController {

    private final EmploymentHistoryService historyService;

    public EmploymentHistoryController(EmploymentHistoryService historyService) {
        this.historyService = historyService;
    }

    /**
     * Alumnus: Gets their own employment history.
     */
    @GetMapping("/me")
    public ResponseEntity<List<EmploymentHistoryDto>> getMyHistory() {
        return ResponseEntity.ok(historyService.getMyHistory());
    }

    /**
     * Alumnus: Adds a new employment history record.
     */
    @PostMapping
    public ResponseEntity<EmploymentHistoryDto> addHistory(@Valid @RequestBody EmploymentHistoryDto historyDto) {
        return ResponseEntity.ok(historyService.addHistory(historyDto));
    }

    /**
     * Alumnus: Updates their own employment history record.
     * Can only update records that are "PENDING_VERIFICATION" or "REJECTED".
     */
    @PutMapping("/{historyId}")
    public ResponseEntity<EmploymentHistoryDto> updateHistory(@PathVariable Long historyId, @Valid @RequestBody EmploymentHistoryDto historyDto) {
        return ResponseEntity.ok(historyService.updateHistory(historyId, historyDto));
    }

    /**
     * Admin: Gets all pending history records for their tenant.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<EmploymentHistoryDto>> getPendingEmploymentHistory() {
        return ResponseEntity.ok(historyService.getPendingHistory());
    }

    /**
     * Admin: Approves or Rejects a history record.
     * Replaces the old "/verify" endpoint.
     */
    @PostMapping("/{historyId}/review")
    public ResponseEntity<EmploymentHistoryDto> reviewHistory(@PathVariable Long historyId, @Valid @RequestBody AdminReviewDto reviewDto) {
        return ResponseEntity.ok(historyService.reviewHistory(historyId, reviewDto));
    }
    
    /**
     * Admin: Gets history for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmploymentHistoryDto>> getHistoryForUser(@PathVariable String userId) {
        return ResponseEntity.ok(historyService.getHistoryForUser(userId));
    }
}

