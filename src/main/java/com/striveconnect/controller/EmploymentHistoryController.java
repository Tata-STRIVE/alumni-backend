package com.striveconnect.controller;

import com.striveconnect.dto.EmploymentHistoryDto;
import com.striveconnect.service.EmploymentHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employment-history")
public class EmploymentHistoryController {

    private final EmploymentHistoryService historyService;

    public EmploymentHistoryController(EmploymentHistoryService historyService) {
        this.historyService = historyService;
    }

    // --- ALUMNI ENDPOINTS ---
    @PostMapping
    public ResponseEntity<EmploymentHistoryDto> addHistory(@RequestBody EmploymentHistoryDto historyDto) {
        EmploymentHistoryDto newHistory = historyService.addEmploymentHistory(historyDto);
        return new ResponseEntity<>(newHistory, HttpStatus.CREATED);
    }

    @PutMapping("/{historyId}")
    public ResponseEntity<EmploymentHistoryDto> updateHistory(
            @PathVariable Long historyId,
            @RequestBody EmploymentHistoryDto historyDto) {
        EmploymentHistoryDto updatedHistory = historyService.updateEmploymentHistory(historyId, historyDto);
        return ResponseEntity.ok(updatedHistory);
    }

    @GetMapping("/me")
    public ResponseEntity<List<EmploymentHistoryDto>> getMyHistory() {
        List<EmploymentHistoryDto> myHistory = historyService.getMyEmploymentHistory();
        return ResponseEntity.ok(myHistory);
    }
    
    // --- ADMIN ENDPOINTS ---
    @GetMapping("/pending")
    public ResponseEntity<List<EmploymentHistoryDto>> getPendingEmploymentHistory() {
        List<EmploymentHistoryDto> pendingHistory = historyService.getPendingHistory();
        return ResponseEntity.ok(pendingHistory);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EmploymentHistoryDto>> getHistoryForUser(@PathVariable String userId) {
        List<EmploymentHistoryDto> userHistory = historyService.getHistoryForUser(userId);
        return ResponseEntity.ok(userHistory);
    }

    @PostMapping("/{historyId}/verify")
    public ResponseEntity<?> verifyHistoryRecord(@PathVariable Long historyId) {
        historyService.verifyEmploymentRecord(historyId);
        return ResponseEntity.ok(Map.of("message", "Employment record verified successfully."));
    }
}

