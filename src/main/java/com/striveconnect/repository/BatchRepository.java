package com.striveconnect.repository;

import com.striveconnect.entity.Batch;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    @Query("SELECT DISTINCT b FROM Batch b " +
           "LEFT JOIN FETCH b.course c " +
           "LEFT JOIN FETCH c.translations " +
           "LEFT JOIN FETCH b.center " +
           "WHERE b.tenantId = :tenantId AND b.startDate >= :date")
    List<Batch> findAllByTenantIdWithDetails(@Param("tenantId") String tenantId, @Param("date") LocalDate date);

    @Query("SELECT DISTINCT b FROM Batch b " +
           "LEFT JOIN FETCH b.center " +
           "LEFT JOIN FETCH b.course c " +
           "LEFT JOIN FETCH c.translations " +
           "WHERE c.courseId = :courseId AND b.startDate >= :date")
    List<Batch> findUpcomingByCourseId(@Param("courseId") Long courseId, @Param("date") LocalDate date);
}
