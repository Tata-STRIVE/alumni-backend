package com.striveconnect.service;

import com.striveconnect.dto.BatchDto;
import com.striveconnect.entity.Batch;
import com.striveconnect.entity.Batch.BatchStatus;
import com.striveconnect.entity.CourseTranslation;
import com.striveconnect.repository.BatchRepository;
import com.striveconnect.util.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchService {

    private final BatchRepository batchRepository;

    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    /**
     * --- NEW METHOD ---
     * Gets all batches for the current tenant, translated.
     * This is used by the Registration page.
     */
    public List<BatchDto> getBatchesByTenant(String languageCode) {
        String tenantId = TenantContext.getCurrentTenant();
        // This query needs to be added to BatchRepository
        List<Batch> batches = batchRepository.findAllByTenantIdWithDetails(tenantId);
        
        return batches.stream()
                .map(batch -> convertToDto(batch, languageCode))
                .collect(Collectors.toList());
    }

    /**
     * Gets all upcoming batches for a specific course, translated.
     */
    public List<BatchDto> getUpcomingBatchesForCourse(Long courseId, String languageCode) {
        List<Batch> batches = batchRepository.findByCourseIdWithDetails(courseId);
        
        return batches.stream()
                .filter(batch -> batch.getStatus() == BatchStatus.UPCOMING)
                .map(batch -> convertToDto(batch, languageCode))
                .collect(Collectors.toList());
    }

    /**
     * Converts a Batch entity to a translated BatchDto.
     */
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

        if (batch.getCourse() != null) {
            // Get the translated course name
            CourseTranslation translation = CourseService.getTranslation(batch.getCourse(), languageCode);
            if (translation != null) {
                dto.setCourseName(translation.getName());
            }
        }
        return dto;
    }
}

