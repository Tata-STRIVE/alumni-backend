package com.striveconnect.repository;

import com.striveconnect.entity.Course;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.translations t WHERE c.tenantId = :tenantId")
    List<Course> findAllByTenantIdWithTranslations(@Param("tenantId") String tenantId);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.translations WHERE c.courseId = :id")
    Course findByIdWithTranslations(@Param("id") Long id);
}
