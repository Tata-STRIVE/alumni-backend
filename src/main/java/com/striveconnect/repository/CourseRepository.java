package com.striveconnect.repository;

import com.striveconnect.dto.CourseDto;
import com.striveconnect.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds all courses for a tenant, joining with translations to pre-fetch them.
     * This is more efficient than firing N+1 queries.
     */
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.translations WHERE c.tenantId = :tenantId")
    List<Course> findAllByTenantIdWithTranslations(String tenantId);

	
}

