package com.striveconnect.controller;

import com.striveconnect.dto.AdminReviewDto;
import com.striveconnect.dto.EmploymentHistoryDto;
import com.striveconnect.service.EmploymentHistoryService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employment-history")
@CrossOrigin
public class EmploymentHistoryController {

    private final EmploymentHistoryService employmentHistoryService;

    public EmploymentHistoryController(EmploymentHistoryService employmentHistoryService) {
        this.employmentHistoryService = employmentHistoryService;
    }

    // 🟢 Add new employment record (Alumnus)
    @PostMapping
    public ResponseEntity<EmploymentHistoryDto> createEmployment(@RequestBody EmploymentHistoryDto dto) {
        EmploymentHistoryDto created = employmentHistoryService.createEmploymentHistory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 🟡 Update employment (Alumnus)
    @PutMapping("/{historyId}")
    public ResponseEntity<EmploymentHistoryDto> updateEmployment(
            @PathVariable Long historyId,
            @RequestBody EmploymentHistoryDto dto) {

        EmploymentHistoryDto updated = employmentHistoryService.updateEmploymentHistory(historyId, dto);
        return ResponseEntity.ok(updated);
    }

    // 🔍 Get my employment history (Alumnus)
    @GetMapping("/me")
    public ResponseEntity<List<EmploymentHistoryDto>> getMyEmploymentHistory() {
        return ResponseEntity.ok(employmentHistoryService.getEmploymentHistoryForCurrentUser());
    }

    // 🔍 Admin: Get pending employment history for review
    @GetMapping("/pending")
    public ResponseEntity<List<EmploymentHistoryDto>> getPendingVerifications() {
        return ResponseEntity.ok(employmentHistoryService.getPendingEmploymentHistory());
    }

	/*
	 * // ✅ Admin: Approve or reject employment record
	 * 
	 * @PostMapping("/{historyId}/verify") public ResponseEntity<Void>
	 * verifyEmployment(
	 * 
	 * @PathVariable Long historyId,
	 * 
	 * @RequestParam boolean approved) {
	 * 
	 * employmentHistoryService.verifyEmploymentHistory(historyId, approved,null);
	 * return ResponseEntity.noContent().build(); }
	 */
    
    
    /**
     * Admin: Approves or Rejects a history record.
     * Replaces the old "/verify" endpoint.
     */
    @PostMapping("/{historyId}/review")
    public ResponseEntity<EmploymentHistoryDto> reviewHistory(@PathVariable Long historyId, @Valid @RequestBody AdminReviewDto reviewDto) {
        return ResponseEntity.ok(employmentHistoryService.reviewHistory(historyId, reviewDto));
    }
    
}
