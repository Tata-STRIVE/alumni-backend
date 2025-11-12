package com.striveconnect.service;

import com.striveconnect.dto.BatchDto;
import com.striveconnect.entity.*;
import com.striveconnect.repository.*;
import com.striveconnect.util.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BatchService {

    private final BatchRepository batchRepository;
    private final CourseRepository courseRepository;
    private final CenterRepository centerRepository;
    private final UserRepository userRepository;

    public BatchService(BatchRepository batchRepository, CourseRepository courseRepository,
                        CenterRepository centerRepository, UserRepository userRepository) {
        this.batchRepository = batchRepository;
        this.courseRepository = courseRepository;
        this.centerRepository = centerRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String mobile = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenant = TenantContext.getCurrentTenant();

        return userRepository.findByMobileNumberAndTenantId(mobile, tenant)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    // 🟢 CREATE
    @Transactional
    public Batch createBatch(BatchDto dto) {
        User user = getCurrentUser();
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        Center center = centerRepository.findById(dto.getCenterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Center not found"));

        Batch batch = new Batch();
        batch.setBatchName(dto.getBatchName());
        batch.setStartDate(dto.getStartDate());
        batch.setEndDate(dto.getEndDate());
        batch.setStatus(Batch.BatchStatus.valueOf(dto.getStatus().toUpperCase()));
        batch.setCourse(course);
        batch.setCenter(center);
        batch.setTenantId(user.getTenantId());
        batch.setActive(true);
        batch.setCreatedBy(user);
        batch.setCreatedAt(LocalDate.now());
        return batchRepository.save(batch);
    }

    // 🟡 UPDATE
    @Transactional
    public Batch updateBatch(Long id, BatchDto dto) {
        User user = getCurrentUser();
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));

        batch.setBatchName(dto.getBatchName());
        batch.setStartDate(dto.getStartDate());
        batch.setEndDate(dto.getEndDate());
        batch.setStatus(Batch.BatchStatus.valueOf(dto.getStatus().toUpperCase()));
        batch.setUpdatedBy(user);
        batch.setUpdatedAt(LocalDate.now());
        return batchRepository.save(batch);
    }

    // 🔴 SOFT DELETE
    @Transactional
    public void softDeleteBatch(Long id) {
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));
        batch.setActive(false);
        batchRepository.save(batch);
    }

    // 🔍 GET ALL BATCHES BY TENANT
    public List<BatchDto> getBatchesByTenant(String lang, String tenantId) {
        List<Batch> batches = batchRepository.findAllByTenantIdWithDetails(tenantId, LocalDate.now().minusDays(5));
        return batches.stream().filter(Batch::isActive)
                .map(b -> BatchDto.fromEntity(b, lang))
                .collect(Collectors.toList());
    }

    // 🔍 UPCOMING BATCHES BY COURSE
    public List<BatchDto> getUpcomingBatchesForCourse(Long courseId, String lang) {
        List<Batch> batches = batchRepository.findUpcomingByCourseId(courseId, LocalDate.now().minusDays(5));
        return batches.stream()
                .filter(b -> b.isActive() && b.getStatus() == Batch.BatchStatus.UPCOMING)
                .map(b -> BatchDto.fromEntity(b, lang))
                .collect(Collectors.toList());
    }

    public Batch getBatchById(Long id) {
        return batchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));
    }
}
