package com.striveconnect.service;

import com.striveconnect.dto.CourseDto;
import com.striveconnect.entity.*;
import com.striveconnect.repository.*;
import com.striveconnect.util.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final BatchRepository batchRepository;

    public CourseService(CourseRepository courseRepository,
                         UserRepository userRepository,
                         BatchRepository batchRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.batchRepository = batchRepository;
    }

    // ✅ Get current authenticated user
    private User getCurrentUser() {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenant = TenantContext.getCurrentTenant();

        return userRepository.findByMobileNumberAndTenantId(mobile, tenant)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    // 🟢 CREATE COURSE
    @Transactional
    public Course createFromDto(CourseDto dto) {
        User user = getCurrentUser();

        Course course = new Course();
        course.setTenantId(user.getTenantId());
        course.setActive(true);
        course.setIconUrl(dto.getIconUrl());
        course.setCreatedBy(user);
        course.setCreatedAt(LocalDateTime.now());
        course.setTranslations(new ArrayList<>());

        if (dto.getTranslations() != null) {
            for (CourseDto.TranslationDto t : dto.getTranslations()) {
                CourseTranslation ct = new CourseTranslation();
                ct.setLanguageCode(t.getLanguageCode());
                ct.setName(t.getName());
                ct.setDescription(t.getDescription());
                ct.setEligibilityCriteria(t.getEligibilityCriteria());
                ct.setCareerPath(t.getCareerPath());
                ct.setCourse(course);
                course.getTranslations().add(ct);
            }
        }

        return courseRepository.save(course);
    }

    // 🟡 UPDATE COURSE
 // CourseService.java (excerpt - updateFromDto)
    @Transactional
    public Course updateFromDto(Long id, CourseDto dto) {
        User user = getCurrentUser();
        Course existing = courseRepository.findByIdWithTranslations(id);

        if (existing == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");

        if (!existing.getTenantId().equals(user.getTenantId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");

        existing.setIconUrl(dto.getIconUrl());
        existing.setUpdatedBy(user);
        existing.setUpdatedAt(LocalDateTime.now());

        // Ensure translations list exists
        if (existing.getTranslations() == null) {
            existing.setTranslations(new ArrayList<>());
        }

        // If DTO provides translations -> merge them
        if (dto.getTranslations() != null && !dto.getTranslations().isEmpty()) {
            // Map existing translations by language for quick lookup
            Map<String, CourseTranslation> existingByLang = existing.getTranslations().stream()
                    .collect(Collectors.toMap(t -> t.getLanguageCode().toUpperCase(), t -> t, (a,b) -> a));

            for (CourseDto.TranslationDto tDto : dto.getTranslations()) {
                if (tDto.getLanguageCode() == null) continue;
                String lang = tDto.getLanguageCode().toUpperCase();

                CourseTranslation existingTrans = existingByLang.get(lang);
                if (existingTrans != null) {
                    // Update only fields provided (defensive)
                    if (tDto.getName() != null) existingTrans.setName(tDto.getName());
                    if (tDto.getDescription() != null) existingTrans.setDescription(tDto.getDescription());
                    if (tDto.getEligibilityCriteria() != null) existingTrans.setEligibilityCriteria(tDto.getEligibilityCriteria());
                    if (tDto.getCareerPath() != null) existingTrans.setCareerPath(tDto.getCareerPath());
                } else {
                    // Create new translation record
                    CourseTranslation ct = new CourseTranslation();
                    ct.setLanguageCode(lang);
                    ct.setName(tDto.getName());
                    ct.setDescription(tDto.getDescription());
                    ct.setEligibilityCriteria(tDto.getEligibilityCriteria());
                    ct.setCareerPath(tDto.getCareerPath());
                    ct.setCourse(existing);
                    existing.getTranslations().add(ct);
                }
            }
            // Do NOT clear existing translations that weren't in DTO — preserve them
        } else {
            // dto.getTranslations() is empty or null
            // If we want to allow a full replace, only then we should clear list.
            // For safety, we do nothing here to avoid accidental deletes.
        }

        return courseRepository.save(existing);
    }


    // 🔴 DELETE (soft)
    @Transactional
    public void softDelete(Long id) {
        User user = getCurrentUser();
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        if (!course.getTenantId().equals(user.getTenantId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");

        course.setActive(false);
        course.setUpdatedBy(user);
        course.setUpdatedAt(LocalDateTime.now());
        courseRepository.save(course);
    }

    // 🔍 GET COURSES (tenant + lang)
    @Transactional(readOnly = true)
    public List<CourseDto> getCourses(String tenantId, String lang, boolean adminView) {
        if (tenantId == null)
            tenantId = TenantContext.getCurrentTenant();

        List<Course> list = courseRepository.findAllByTenantIdWithTranslations(tenantId);
        return list.stream()
                .filter(Course::isActive)
                .map(c -> convertToDto(c, lang, adminView))
                .collect(Collectors.toList());
    }

    // 🔍 GET COURSE BY ID (user mode)
    @Transactional(readOnly = true)
    public CourseDto getCourseById(Long id) {
        Course course = courseRepository.findByIdWithTranslations(id);
        if (course == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        return convertToFullDto(course);
    }

    // 🔍 NEW — ADMIN: Get all translations
    @Transactional(readOnly = true)
    public CourseDto getCourseWithAllTranslations(Long id) {
        Course course = courseRepository.findByIdWithTranslations(id);
        if (course == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        return convertToFullDto(course);
    }

    // 🧭 Helper — for UI view
    private CourseDto convertToDto(Course course, String lang, boolean adminView) {
        CourseDto dto = new CourseDto();
        dto.setCourseId(course.getCourseId());
        dto.setTenantId(course.getTenantId());
        dto.setIconUrl(course.getIconUrl());
        dto.setActive(course.isActive());

        if (course.getTranslations() == null)
            course.setTranslations(new ArrayList<>());

        CourseTranslation trans = course.getTranslations().stream()
                .filter(t -> t.getLanguageCode().equalsIgnoreCase(lang))
                .findFirst()
                .orElseGet(() -> course.getTranslations().stream()
                        .filter(t -> t.getLanguageCode().equalsIgnoreCase("EN"))
                        .findFirst()
                        .orElse(null));

        if (adminView) {
            dto.setTranslations(course.getTranslations()
                    .stream()
                    .map(CourseDto.TranslationDto::fromEntity)
                    .collect(Collectors.toList()));
        } else if (trans != null) {
            dto.setName(trans.getName());
            dto.setDescription(trans.getDescription());
            dto.setEligibilityCriteria(trans.getEligibilityCriteria());
            dto.setCareerPath(trans.getCareerPath());
            dto.setTranslations(List.of(CourseDto.TranslationDto.fromEntity(trans)));
        }

        return dto;
    }

    private CourseDto convertToFullDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setCourseId(course.getCourseId());
        dto.setTenantId(course.getTenantId());
        dto.setIconUrl(course.getIconUrl());
        dto.setActive(course.isActive());
        dto.setTranslations(course.getTranslations().stream()
                .map(CourseDto.TranslationDto::fromEntity)
                .collect(Collectors.toList()));
        return dto;
    }
}
