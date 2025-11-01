package com.striveconnect.controller;

import com.striveconnect.dto.BatchDto;
import com.striveconnect.dto.ContentPostCreateDto;
import com.striveconnect.dto.ContentPostDto;
import com.striveconnect.dto.CourseDto;
import com.striveconnect.service.BatchService;
import com.striveconnect.service.CourseService;
import com.striveconnect.util.TenantContext;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final BatchService batchService;

    public CourseController(CourseService courseService, BatchService batchService) {
        this.courseService = courseService;
        this.batchService = batchService;
    }

    /**
     * Gets all courses for the user's tenant, translated.
     * Example: GET /api/courses?lang=hi
     */
    @GetMapping
    public ResponseEntity<List<CourseDto>> getTenantCourses(
            @RequestParam(defaultValue = "en") String lang , @RequestParam String tenantId) {
        // Tenant ID is set in TenantContext by the JwtRequestFilter
        // For public access, this might need adjustment
        List<CourseDto> courses = courseService.getAllCourses(lang,tenantId);
        return ResponseEntity.ok(courses);
    }

    /**
     * Gets all UPCOMING batches for a specific course, translated.
     * Example: GET /api/courses/1/batches?lang=en
     */
    @GetMapping("/{courseId}/batches")
    public ResponseEntity<List<BatchDto>> getUpcomingBatches(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "en") String lang) {
        List<BatchDto> batches = batchService.getUpcomingBatchesForCourse(courseId, lang);
        return ResponseEntity.ok(batches);
    }
    
   
}

