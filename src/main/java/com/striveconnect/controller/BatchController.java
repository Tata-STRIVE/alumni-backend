package com.striveconnect.controller;

import com.striveconnect.dto.BatchDto;
import com.striveconnect.entity.Batch;
import com.striveconnect.service.BatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@CrossOrigin
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    // 🟢 CREATE BATCH
    @PostMapping
    public ResponseEntity<Batch> createBatch(@RequestBody BatchDto dto) {
        Batch created = batchService.createBatch(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 🟡 UPDATE BATCH
    @PutMapping("/{batchId}")
    public ResponseEntity<Batch> updateBatch(@PathVariable Long batchId, @RequestBody BatchDto dto) {
        Batch updated = batchService.updateBatch(batchId, dto);
        return ResponseEntity.ok(updated);
    }

    // 🔴 SOFT DELETE
    @DeleteMapping("/{batchId}")
    public ResponseEntity<Void> deleteBatch(@PathVariable Long batchId) {
        batchService.softDeleteBatch(batchId);
        return ResponseEntity.noContent().build();
    }

    // 🔍 GET ALL BATCHES BY TENANT
    @GetMapping
    public ResponseEntity<List<BatchDto>> getBatchesByTenant(
            @RequestParam String tenantId,
            @RequestParam(defaultValue = "en") String lang) {

        List<BatchDto> batches = batchService.getBatchesByTenant(lang, tenantId);
        return ResponseEntity.ok(batches);
    }

    // 🔍 GET UPCOMING BATCHES FOR COURSE
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<BatchDto>> getUpcomingBatchesForCourse(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "en") String lang) {

        List<BatchDto> batches = batchService.getUpcomingBatchesForCourse(courseId, lang);
        return ResponseEntity.ok(batches);
    }

    // 🔍 GET BATCH BY ID (for Edit)
    @GetMapping("/{batchId}")
    public ResponseEntity<Batch> getBatchById(@PathVariable Long batchId) {
        Batch batch = batchService.getBatchById(batchId);
        return ResponseEntity.ok(batch);
    }
}
