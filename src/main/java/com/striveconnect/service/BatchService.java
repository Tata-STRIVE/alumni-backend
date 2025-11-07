package com.striveconnect.service;

import com.striveconnect.dto.BatchCreateDto;
import com.striveconnect.dto.BatchDto;
import com.striveconnect.dto.BatchCreateDto;
import com.striveconnect.entity.Batch;
import com.striveconnect.entity.Batch.BatchStatus;
import com.striveconnect.entity.Center;
import com.striveconnect.entity.Course;
import com.striveconnect.entity.CourseTranslation;
import com.striveconnect.entity.User;
import com.striveconnect.repository.BatchRepository;
import com.striveconnect.repository.CenterRepository;
import com.striveconnect.repository.CourseRepository;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchService {

    private final BatchRepository batchRepository;
    private final UserRepository userRepository;
    private final CenterRepository centerRepository;
    private final CourseRepository courseRepository;
    
    

    public BatchService(BatchRepository batchRepository, UserRepository userRepository,
    		CenterRepository centerRepository ,  CourseRepository courseRepository) {
        this.batchRepository = batchRepository;
        this.userRepository = userRepository;
        this.centerRepository=centerRepository;
        this.courseRepository=courseRepository;
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

    // --------------------------------------------------------------------------------------------
    // 🟢 CREATE
    // --------------------------------------------------------------------------------------------
    @Transactional
    public Batch createBatch(BatchCreateDto batchCreateDto) {
        User currentUser = getCurrentUser();
        
        Batch batch = new Batch();

        if (currentUser.getTenantId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant information missing for user.");
        }
        
		/*
		 * BatchCreateDto batchCreateDto = new BatchCreateDto();
		 * batchCreateDto.setTenantId(currentUser.getTenantId());
		 * batchCreateDto.setCreatedByUserId(currentUser.getUserId());
		 * batchCreateDto.setCreatedAt(batch.getCreatedAt());
		 * batchCreateDto.setIsActive(true);
		 * batchCreateDto.setBatchName(batch.getBatchName());
		 * batchCreateDto.setCourseId(batch.getCourse().getCourseId().toString());
		 * batchCreateDto.setCenterId(batch.getCenter().getCenterId().toString());
		 * batchCreateDto.setStatus(batch.getStatus().name());
		 */
       
        
        System.out.println("batchCreateDto.getCourseId()"  +batchCreateDto.getCourseId());
        System.out.println("batchCreateDto.getCenetrId()"  +batchCreateDto.getCenterId());


        batch.setCreatedAt(LocalDate.now());
        batch.setBatchName(batchCreateDto.getBatchName());
        batch.setStartDate(batchCreateDto.getStartDate());
        batch.setEndDate(batchCreateDto.getEndDate());
        batch.setStatus(Batch.BatchStatus.valueOf(batchCreateDto.getStatus().toUpperCase()));

        // Fetch linked entities
        Course course = courseRepository.findById(batchCreateDto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + batchCreateDto.getCourseId()));
        Center center = centerRepository.findById(batchCreateDto.getCenterId())
                .orElseThrow(() -> new RuntimeException("Center not found with ID: " + batchCreateDto.getCenterId()));

        batch.setCourse(course);
        batch.setCenter(center);
        batch.setTenantId(course.getTenantId());
        
        batch.setTenantId(currentUser.getTenantId());
        batch.setActive(true);        
        batch.setCreatedBy(currentUser);
        System.out.println("Batch Details  ==> " +batch);
        
        

        System.out.println(batch.toString());

        return batchRepository.save(batch);
    }

    // --------------------------------------------------------------------------------------------
    // 🟡 UPDATE
    // --------------------------------------------------------------------------------------------
    @Transactional
    public Batch updateBatch(Long batchId, BatchDto updatedBatch) {
        User currentUser = getCurrentUser();
     

        Batch existingBatch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found."));

        if (!existingBatch.getTenantId().equals(currentUser.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for this batch.");
        }

        existingBatch.setBatchName(updatedBatch.getBatchName());
        existingBatch.setStartDate(updatedBatch.getStartDate());
        existingBatch.setEndDate(updatedBatch.getEndDate());
        existingBatch.setStatus(Batch.BatchStatus.valueOf(updatedBatch.getStatus().toUpperCase()));
        
        // Fetch linked entities
        Course course = courseRepository.findById(updatedBatch.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + updatedBatch.getCourseId()));
        Center center = centerRepository.findById(updatedBatch.getCenterId())
                .orElseThrow(() -> new RuntimeException("Center not found with ID: " + updatedBatch.getCenterId()));


        existingBatch.setCourse(course);
        existingBatch.setCenter(center);
        existingBatch.setUpdatedBy(currentUser);
        existingBatch.setUpdatedAt(LocalDate.now());

        return batchRepository.save(existingBatch);
    }

    // --------------------------------------------------------------------------------------------
    // 🔴 SOFT DELETE
    // --------------------------------------------------------------------------------------------
    @Transactional
    public void softDeleteBatch(Long batchId) {
        User currentUser = getCurrentUser();

        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found."));

        if (!batch.getTenantId().equals(currentUser.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied for this batch.");
        }

        batch.setActive(false);
        batch.setUpdatedBy(currentUser);
        batch.setUpdatedAt(LocalDate.now());

        batchRepository.save(batch);
    }

    // --------------------------------------------------------------------------------------------
    // 🔍 READ METHODS
    // --------------------------------------------------------------------------------------------

    /**
     * Gets all active batches for the current tenant.
     */
    public List<BatchDto> getBatchesByTenant(String languageCode, String tenantId) {
    	if(tenantId == null)
         tenantId = TenantContext.getCurrentTenant();    	
    	LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);
    	List<Batch> batches = batchRepository.findAllByTenantIdWithDetails(tenantId, fiveDaysAgo);
    	
      //  List<Batch> batches = batchRepository.findAllByTenantIdWithDetails(tenantId);

        return batches.stream()
                .filter(Batch::isActive)
                .map(batch -> convertToDto(batch, languageCode))
                .collect(Collectors.toList());
    }

    /**
     * Gets all upcoming batches for a specific course.
     */
    public List<BatchDto> getUpcomingBatchesForCourse(Long courseId, String languageCode) {
        String tenantId = TenantContext.getCurrentTenant();
        
        
        LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);
        List<Batch> batches = batchRepository.findByCourseIdWithDetails(courseId, fiveDaysAgo);
     //   List<Batch> batches = batchRepository.findByCourseIdWithDetails(courseId);

        return batches.stream()
                .filter(b -> tenantId.equals(b.getTenantId()))
                .filter(b -> b.getStatus() == BatchStatus.UPCOMING)
                .filter(Batch::isActive)
                .map(batch -> convertToDto(batch, languageCode))
                .collect(Collectors.toList());
    }
    
    
    
    // ✅ Fetch single batch by ID (for editing)
    public Batch getBatchById(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));

        
        // Fetch linked entities
        Course course = courseRepository.findById(batch.getCourse().getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + batch.getCourse().getCourseId()));
        Center center = centerRepository.findById(batch.getCenter().getCenterId())
                .orElseThrow(() -> new RuntimeException("Center not found with ID: " + batch.getCenter().getCenterId()));

        
        // Map transient fields for frontend editing
        if (batch.getCourse() != null) batch.setCourse(course);
        if (batch.getCenter() != null) batch.setCenter(center);

        return batch;
    }
    

    // --------------------------------------------------------------------------------------------
    // 🧾 DTO CONVERSION
    // --------------------------------------------------------------------------------------------
    private BatchDto convertToDto(Batch batch, String languageCode) {
        BatchDto dto = new BatchDto();
        dto.setBatchId(batch.getBatchId());
        dto.setBatchName(batch.getBatchName());
        dto.setStartDate(batch.getStartDate());
        dto.setEndDate(batch.getEndDate());
        dto.setStatus(batch.getStatus().name());

        if (batch.getCenter() != null) {
            dto.setCenterName(batch.getCenter().getName());
            dto.setCenterCity(batch.getCenter().getCity());
        }
        if (batch.getCenter() != null) {
            dto.setCenterId(batch.getCenter().getCenterId());
            dto.setCourseId(batch.getCourse().getCourseId());
        }

        if (batch.getCourse() != null && batch.getCourse().getTranslations() != null) {
            CourseTranslation translation = CourseService.getTranslation(batch.getCourse(), languageCode);
            dto.setCourseName(translation != null ? translation.getName() : "Translation missing");
        }

        return dto;
    }
}
