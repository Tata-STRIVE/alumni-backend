package com.striveconnect.controller;

import com.striveconnect.dto.CourseDto;
import com.striveconnect.entity.Course;
import com.striveconnect.service.CourseService;
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

    @GetMapping
    public ResponseEntity<List<CourseDto>> getCourses(
            @RequestParam(defaultValue = "en") String lang,
            @RequestParam(required = false) String tenantId) {
        return ResponseEntity.ok(courseService.getAllCourses(lang, tenantId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }


    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course course) {
        return ResponseEntity.ok(courseService.updateCourse(id, course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.softDeleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
