package com.striveconnect.controller;

import com.striveconnect.dto.BatchCreateDto;
import com.striveconnect.dto.BatchDto;
import com.striveconnect.entity.Batch;
import com.striveconnect.service.BatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    /**
     * 🟢 CREATE a new batch
     */
    @PostMapping
    public ResponseEntity<Batch> createBatch(@RequestBody BatchCreateDto batchCreateDto) {
    	
    	System.out.println(batchCreateDto.getCenterId());
    	System.out.println(batchCreateDto.getCourseId());

        Batch created = batchService.createBatch(batchCreateDto);
        return ResponseEntity.ok(created);
    }

    /**
     * 🟡 UPDATE an existing batch
     */
    @PutMapping("/{id}")
    public ResponseEntity<Batch> updateBatch(@PathVariable Long id, @RequestBody BatchDto batchDto) {
        Batch updated = batchService.updateBatch(id, batchDto);
        return ResponseEntity.ok(updated);
    }

    /**
     * 🔴 SOFT DELETE batch
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable Long id) {
        batchService.softDeleteBatch(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 🔍 GET all batches for tenant
     * Example: GET /api/batches?lang=en
     */
    @GetMapping
    public ResponseEntity<List<BatchDto>> getTenantBatches(
            @RequestParam(defaultValue = "en") String lang, @RequestParam String tenantId ) {
        List<BatchDto> batches = batchService.getBatchesByTenant(lang,tenantId);
        return ResponseEntity.ok(batches);
    }

    /**
     * 🔍 GET all upcoming batches for a given course
     * Example: /api/batches/course/5?lang=hi
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<BatchDto>> getUpcomingBatches(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "en") String lang) {
        List<BatchDto> batches = batchService.getUpcomingBatchesForCourse(courseId, lang);
        return ResponseEntity.ok(batches);
    }
}
