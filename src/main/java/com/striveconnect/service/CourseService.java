package com.striveconnect.service;

import com.striveconnect.dto.BatchCreateDto;
import com.striveconnect.dto.BatchDto;
import com.striveconnect.dto.CourseDto;
import com.striveconnect.entity.Batch;
import com.striveconnect.entity.Course;
import com.striveconnect.entity.CourseTranslation;
import com.striveconnect.entity.User;
import com.striveconnect.repository.CourseRepository;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final BatchService batchService;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, BatchService batchService, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.batchService = batchService;
        this.userRepository = userRepository;
    }

    // --------------------------------------------------------------------------------------------
    // 🔐 Helper: Get current authenticated user with tenant context
    // --------------------------------------------------------------------------------------------
    private User getCurrentUser() {
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();

        return userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Authenticated user not found or session expired."));
    }

    /**
     * Safe version that returns null if no authenticated user exists.
     */
    private User getCurrentUserOrNull() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }

        String mobileNumber = auth.getName();
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId).orElse(null);
    }

    // --------------------------------------------------------------------------------------------
    // 🟢 CREATE
    // --------------------------------------------------------------------------------------------
    @Transactional
    public Course createCourse(Course course) {
        User currentUser = getCurrentUser();

        if (currentUser.getTenantId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant information missing for user.");
        }

        course.setTenantId(currentUser.getTenantId());
        course.setCreatedBy(currentUser);
        course.setCreatedAt(LocalDateTime.now());
        course.setActive(true);

        if (course.getTranslations() != null) {
            course.getTranslations().forEach(t -> t.setCourse(course));
        }

        return courseRepository.save(course);
    }

    // --------------------------------------------------------------------------------------------
    // 🟡 UPDATE
    // --------------------------------------------------------------------------------------------
    @Transactional
    public Course updateCourse(Long courseId, Course updatedCourse) {
        User currentUser = getCurrentUser();

        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found."));

        if (!existingCourse.getTenantId().equals(currentUser.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for this course.");
        }

        existingCourse.setIconUrl(updatedCourse.getIconUrl());
        existingCourse.setUpdatedBy(currentUser);
        existingCourse.setUpdatedAt(LocalDateTime.now());

        existingCourse.getTranslations().clear();
        if (updatedCourse.getTranslations() != null) {
            updatedCourse.getTranslations().forEach(t -> t.setCourse(existingCourse));
            existingCourse.getTranslations().addAll(updatedCourse.getTranslations());
        }

        return courseRepository.save(existingCourse);
    }

    // --------------------------------------------------------------------------------------------
    // 🔴 SOFT DELETE
    // --------------------------------------------------------------------------------------------
    @Transactional
    public void softDeleteCourse(Long courseId) {
        User currentUser = getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found."));

        if (!course.getTenantId().equals(currentUser.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for this course.");
        }

        course.setActive(false);
        course.setUpdatedBy(currentUser);
        course.setUpdatedAt(LocalDateTime.now());

        courseRepository.save(course);
    }

    // --------------------------------------------------------------------------------------------
    // 🔍 READ METHODS
    // --------------------------------------------------------------------------------------------
    public List<CourseDto> getAllCourses(String languageCode, String tenantId) {
        System.out.println("tenantId ==> getAllCourses " + tenantId);
        if (tenantId == null) {
            tenantId = TenantContext.getCurrentTenant();
        }
        List<Course> courses = courseRepository.findAllByTenantIdWithTranslations(tenantId);

        return courses.stream()
                .filter(Course::isActive)
                .map(course -> convertToDto(course, languageCode, true))
                .collect(Collectors.toList());
    }

    // --------------------------------------------------------------------------------------------
    // 🧩 DTO Conversion (Safe for Public Access)
    // --------------------------------------------------------------------------------------------
    public CourseDto convertToDto(Course course, String languageCode, boolean includeBatches) {
        CourseDto dto = new CourseDto();
        dto.setCourseId(course.getCourseId());
        dto.setIconUrl(course.getIconUrl());
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());

        // ✅ Use the safe version to avoid 401 for anonymous users
        User user = getCurrentUserOrNull();
        if (user != null) {
            dto.setCreatedBy(user.getUserId());
            dto.setUpdatedBy(user.getUserId());
        }

        CourseTranslation translation = getTranslation(course, languageCode);
        if (translation != null) {
            dto.setName(translation.getName());
            dto.setDescription(translation.getDescription());
            dto.setEligibilityCriteria(translation.getEligibilityCriteria());
            dto.setCareerPath(translation.getCareerPath());
        } else {
            dto.setName("Translation Missing (" + languageCode.toUpperCase() + ")");
            dto.setDescription("Content unavailable in " + languageCode.toUpperCase());
        }

        if (includeBatches) {
            List<BatchDto> upcomingBatches = batchService.getUpcomingBatchesForCourse(course.getCourseId(), languageCode);
            dto.setUpcomingBatches(upcomingBatches);
        }

        return dto;
    }

    // --------------------------------------------------------------------------------------------
    // 🌐 Translation Utility
    // --------------------------------------------------------------------------------------------
    public static CourseTranslation getTranslation(Course course, String languageCode) {
        if (course == null || course.getTranslations() == null) return null;

        return course.getTranslations().stream()
                .filter(t -> t.getLanguageCode().equalsIgnoreCase(languageCode))
                .findFirst()
                .orElse(course.getTranslations().stream()
                        .filter(t -> t.getLanguageCode().equalsIgnoreCase("en"))
                        .findFirst()
                        .orElse(null));
    }
    
    
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Course not found"));
    }

}
