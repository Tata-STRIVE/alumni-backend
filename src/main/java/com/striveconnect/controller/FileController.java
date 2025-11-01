package com.striveconnect.controller;

import com.striveconnect.exception.StorageException;
import com.striveconnect.service.StorageService;
import com.striveconnect.util.TenantContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Handles file uploads (e.g., profile pictures, employment proofs).
     * The file is stored under: tenantId/userId/filename.ext
     *
     * @param file The file to upload.
     * @param authentication The current authenticated user.
     * @return A map containing the direct URL path to the stored file.
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> handleFileUpload(@RequestParam("file") MultipartFile file, Authentication authentication) {
        String userId = authentication.getName(); // Assumes the principal name is the mobile number/userId
        String tenantId = TenantContext.getCurrentTenant();

        if (file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "File cannot be empty.");
        }

        try {
            // Store the file and get the relative URL back
            String fileUrl = storageService.store(file, tenantId, userId);
            
            // Return the URL to the frontend for saving in the User profile
            return ResponseEntity.ok(Map.of("fileUrl", fileUrl));
        } catch (StorageException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Serves files based on the relative path provided by the frontend.
     * This endpoint must be public in SecurityConfig.
     *
     * @param tenantId The tenant ID (part of the URL path).
     * @param userId The user ID (part of the URL path).
     * @param filename The name of the file.
     * @return The file content as a Resource.
     */
    @GetMapping("/download/{tenantId}/{userId}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @PathVariable String tenantId,
            @PathVariable String userId,
            @PathVariable String filename) {

        // Reconstruct the relative path used in the store method
        String relativePath = tenantId + "/" + userId + "/" + filename;

        try {
            Resource file = storageService.loadAsResource(relativePath);
            String contentType = Files.probeContentType(file.getFile().toPath());
            
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    .body(file);

        } catch (StorageException e) {
            // File not found is handled here
            throw new ResponseStatusException(404, "File not found: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Error reading file.");
        }
    }
}
