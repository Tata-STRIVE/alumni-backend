package com.striveconnect.repository;

import com.striveconnect.entity.CourseTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseTranslationRepository extends JpaRepository<CourseTranslation, Long> {

    /**
     * Get all translations for a given course.
     */
    List<CourseTranslation> findByCourse_CourseId(Long courseId);

    /**
     * Get a translation for a course in a specific language.
     */
    CourseTranslation findByCourse_CourseIdAndLanguageCode(Long courseId, String languageCode);

    /**
     * Get all translations for a tenant (useful for bulk management / admin view).
     */
    List<CourseTranslation> findByCourse_TenantId(String tenantId);
}
