package com.striveconnect.repository;

import com.striveconnect.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

	
	
	
	
    /**
     * Finds all batches for a specific course, joining center and course info.
     */
    @Query("SELECT b FROM Batch b " +
           "LEFT JOIN FETCH b.course c " +
           "LEFT JOIN FETCH c.translations " +
           "LEFT JOIN FETCH b.center " +
           "WHERE b.course.courseId = :courseId  ")
    List<Batch> findByCourseIdWithDetails(Long courseId);

    
    
    
    @Query("""
    	    SELECT b FROM Batch b
    	    LEFT JOIN FETCH b.course c
    	    LEFT JOIN FETCH c.translations
    	    LEFT JOIN FETCH b.center
    	    WHERE b.course.courseId = :courseId
    	      AND b.startDate >= :dateThreshold
    	""")
    	List<Batch> findByCourseIdWithDetails(
    	    @Param("courseId") Long courseId,
    	    @Param("dateThreshold") LocalDate dateThreshold
    	);
    
    @Query("""
    	    SELECT b FROM Batch b
    	    LEFT JOIN FETCH b.course c
    	    LEFT JOIN FETCH c.translations
    	    LEFT JOIN FETCH b.center
    	    WHERE b.tenantId = :tenantId
    	      AND b.startDate >= :dateThreshold
    	""")
    	List<Batch> findAllByTenantIdWithDetails(
    	    @Param("tenantId") String tenantId,
    	    @Param("dateThreshold") LocalDate dateThreshold
    	);
    
    
    /**
     * --- NEW METHOD ---
     * Finds all batches for a specific tenant, joining all details.
     */
    @Query("SELECT b FROM Batch b " +
           "LEFT JOIN FETCH b.course c " +
           "LEFT JOIN FETCH c.translations " +
           "LEFT JOIN FETCH b.center " +
           "WHERE b.tenantId = :tenantId ")
    List<Batch> findAllByTenantIdWithDetails(String tenantId);
}

