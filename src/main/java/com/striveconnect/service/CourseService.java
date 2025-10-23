package com.striveconnect.service;

import com.striveconnect.dto.BatchDto;
import com.striveconnect.dto.CourseDto;
import com.striveconnect.entity.Batch;
import com.striveconnect.entity.Course;
import com.striveconnect.entity.CourseTranslation;
import com.striveconnect.repository.CourseRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final BatchService batchService; // We'll need this to get upcoming batches

    public CourseService(CourseRepository courseRepository, BatchService batchService) {
        this.courseRepository = courseRepository;
        this.batchService = batchService;
    }

    /**
     * Gets all courses for the current tenant, translated to the specified language.
     */
    public List<CourseDto> getAllCourses(String languageCode) {
        String tenantId = TenantContext.getCurrentTenant();
        List<Course> courses = courseRepository.findAllByTenantIdWithTranslations(tenantId);
        
        return courses.stream()
                .map(course -> convertToDto(course, languageCode, true)) // true = fetch upcoming batches
                .collect(Collectors.toList());
    }

    /**
     * Converts a Course entity to a translated CourseDto.
     */
    private CourseDto convertToDto(Course course, String languageCode, boolean fetchBatches) {
        CourseDto dto = new CourseDto();
        dto.setCourseId(course.getCourseId());
        dto.setIconUrl(course.getIconUrl());

        // Find the correct translation
        CourseTranslation translation = getTranslation(course, languageCode);
        
        dto.setName(translation.getName());
        dto.setDescription(translation.getDescription());
        dto.setEligibilityCriteria(translation.getEligibilityCriteria());
        dto.setCareerPath(translation.getCareerPath());

        // As requested, if they click a course, show upcoming batches
        if (fetchBatches) {
            List<BatchDto> upcomingBatches = batchService.getUpcomingBatchesForCourse(course.getCourseId(), languageCode);
            dto.setUpcomingBatches(upcomingBatches);
        }
        
        return dto;
    }

    /**
     * Helper to find the correct translation, defaulting to 'en'.
     */
    public static CourseTranslation getTranslation(Course course, String languageCode) {
        return course.getTranslations().stream()
                .filter(t -> t.getLanguageCode().equalsIgnoreCase(languageCode))
                .findFirst()
                .orElse(
                    // Default to English if the requested language isn't found
                    course.getTranslations().stream()
                        .filter(t -> t.getLanguageCode().equalsIgnoreCase("en"))
                        .findFirst()
                        .orElse(null) // Or throw an exception if 'en' is also missing
                );
    }
}

