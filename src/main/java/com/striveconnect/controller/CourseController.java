package com.striveconnect.controller;

import com.striveconnect.dto.CourseDto;
import com.striveconnect.entity.Course;
import com.striveconnect.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // 🟢 CREATE
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody CourseDto dto) {
        Course created = courseService.createFromDto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 🟡 UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody CourseDto dto) {
        Course updated = courseService.updateFromDto(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 🔴 DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    // 🔍 GET ALL
    @GetMapping
    public ResponseEntity<List<CourseDto>> getCourses(
            @RequestParam String tenantId,
            @RequestParam(defaultValue = "en") String lang,
            @RequestParam(defaultValue = "false") boolean adminView) {
        return ResponseEntity.ok(courseService.getCourses(tenantId, lang, adminView));
    }

    // 🔍 GET ONE (standard)
    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // 🧭 NEW ADMIN ENDPOINT
    @GetMapping("/{id}/all-translations")
    public ResponseEntity<CourseDto> getCourseWithAllTranslations(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseWithAllTranslations(id));
    }
}
