package com.striveconnect.repository;

import com.striveconnect.entity.CourseTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseTranslationRepository extends JpaRepository<CourseTranslation, Long> {
    // We will primarily access translations through the Course entity
}
