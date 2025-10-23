package com.striveconnect.controller;

import com.striveconnect.dto.BatchDto;
import com.striveconnect.service.BatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
     * --- NEW ENDPOINT ---
     * Gets all batches for the user's tenant, translated.
     * This is used for the registration page.
     * Example: GET /api/batches?lang=hi
     */
    @GetMapping
    public ResponseEntity<List<BatchDto>> getTenantBatches(
            @RequestParam(defaultValue = "en") String lang) {
        List<BatchDto> batches = batchService.getBatchesByTenant(lang);
        return ResponseEntity.ok(batches);
    }
}

