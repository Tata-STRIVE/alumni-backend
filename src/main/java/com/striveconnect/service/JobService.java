package com.striveconnect.service;

import com.striveconnect.dto.ApplicationDto;
import com.striveconnect.dto.CreateJobRequestDto;
import com.striveconnect.dto.JobApplicationDto;
import com.striveconnect.dto.JobPostingDto;
import com.striveconnect.dto.JobReviewDto;

import com.striveconnect.dto.UpdateApplicationStatusDto;
import com.striveconnect.entity.JobApplication;
import com.striveconnect.entity.JobPosting;
import com.striveconnect.entity.User;
import com.striveconnect.repository.JobApplicationRepository;
import com.striveconnect.repository.JobPostingRepository;
import com.striveconnect.repository.UserRepository;
import com.striveconnect.util.TenantContext;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    public JobService(JobPostingRepository jobPostingRepository, JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * NEW: Gets all job applications for the currently logged-in alumnus.
     */
    public List<JobApplicationDto> getMyJobApplications() {
        User currentUser = getCurrentUser();
        List<JobApplication> applications = jobApplicationRepository.findByUser(currentUser);
        return applications.stream()
                .map(this::convertJobApplicationToDto)
                .collect(Collectors.toList());
    }
    
    // NEW: DTO converter for a job application
    private JobApplicationDto convertJobApplicationToDto(JobApplication app) {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setApplicationId(app.getApplicationId());
        dto.setStatus(app.getStatus().name());
        dto.setAppliedAt(app.getAppliedAt());
        
        JobPosting job = app.getJobPosting();
        if (job != null) {
            dto.setJobId(job.getJobId());
            dto.setJobTitle(job.getTitle());
            dto.setCompanyName(job.getCompanyName());
        }
        return dto;
    }

    /**
     * Alumnus: Submits a job for admin verification.
     */
    public JobPostingDto submitJobForVerification(CreateJobRequestDto createDto) {
        User currentUser = getCurrentUser();

        JobPosting jobPosting = new JobPosting();
        jobPosting.setTenantId(currentUser.getTenantId());
        jobPosting.setTitle(createDto.getTitle());
        jobPosting.setCompanyName(createDto.getCompanyName());
        jobPosting.setLocation(createDto.getLocation());
        jobPosting.setDescription(createDto.getDescription());
        jobPosting.setAuthor(currentUser);
        jobPosting.setStatus(JobPosting.JobStatus.PENDING_VERIFICATION); // Set status for admin review
        jobPosting.setCreatedAt(LocalDateTime.now());
        
        JobPosting savedJob = jobPostingRepository.save(jobPosting);
        return convertToDto(savedJob);
    }

    /**
     * Admin: Gets all jobs pending verification for the current tenant.
     */
    public List<JobPostingDto> getPendingJobs() {
        String tenantId = TenantContext.getCurrentTenant();
        List<JobPosting> pendingJobs = jobPostingRepository.findByTenantIdAndStatus(tenantId, JobPosting.JobStatus.PENDING_VERIFICATION);
        return pendingJobs.stream().map(this::convertToDtoWithAuthor).collect(Collectors.toList());
    }

    /**
     * Admin: Approves a job posting, changing its status to OPEN.
     */
    public void approveJob(Long jobId) {
        JobPosting jobToApprove = findJobAndVerifyTenant(jobId);

        if (jobToApprove.getStatus() != JobPosting.JobStatus.PENDING_VERIFICATION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This job is not pending verification.");
        }

        jobToApprove.setStatus(JobPosting.JobStatus.OPEN);
        jobPostingRepository.save(jobToApprove);
    }

    
    /**
     * Admin: Approves a job posting, changing its status to OPEN.
     */
    public void reviewJob(Long jobId , JobReviewDto jobReviewDto ) {
        JobPosting jobToApprove = findJobAndVerifyTenant(jobId);

        if (jobToApprove.getStatus() != JobPosting.JobStatus.PENDING_VERIFICATION) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This job is not pending verification.");
        }

        String reviewStatus = jobReviewDto.getStatus();
        jobToApprove.setStatus(JobPosting.JobStatus.valueOf(reviewStatus));
        jobToApprove.setRejectionRemarks(jobReviewDto.getRejectionRemarks());

        jobPostingRepository.save(jobToApprove);
        
    }
    // --- DTO Converters & Helpers ---
    private JobPostingDto convertToDtoWithAuthor(JobPosting jobPosting) {
        JobPostingDto dto = convertToDto(jobPosting);
        if (jobPosting.getAuthor() != null) {
            dto.setAuthorName(jobPosting.getAuthor().getFullName());
        }
        return dto;
    }
    
    // --- All other existing methods in JobService ---
    public List<JobPostingDto> getOpenJobsForCurrentTenant() {
        String tenantId = TenantContext.getCurrentTenant();
        List<JobPosting> jobPostings = jobPostingRepository.findByTenantIdAndStatus(tenantId, JobPosting.JobStatus.OPEN);
        return jobPostings.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    public void applyToJob(Long jobId) {
        User currentUser = getCurrentUser();
        JobPosting jobPosting = findJobAndVerifyTenant(jobId);
        if (jobApplicationRepository.existsByJobPostingAndUser(jobPosting, currentUser)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already applied for this job");
        }
        JobApplication application = new JobApplication();
        application.setJobPosting(jobPosting);
        application.setUser(currentUser);
        application.setStatus(JobApplication.ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());
        jobApplicationRepository.save(application);
    }
    public JobPostingDto createJobPosting(CreateJobRequestDto createDto) {
        User adminUser = getCurrentUser();
        JobPosting jobPosting = new JobPosting();
        jobPosting.setTenantId(adminUser.getTenantId());
        jobPosting.setTitle(createDto.getTitle());
        jobPosting.setCompanyName(createDto.getCompanyName());
        jobPosting.setLocation(createDto.getLocation());
        jobPosting.setDescription(createDto.getDescription());
        jobPosting.setAuthor(adminUser);
        jobPosting.setStatus(JobPosting.JobStatus.OPEN);
        jobPosting.setCreatedAt(LocalDateTime.now());
        JobPosting savedJob = jobPostingRepository.save(jobPosting);
        return convertToDto(savedJob);
    }
    public List<ApplicationDto> getApplicationsForJob(Long jobId) {
        JobPosting jobPosting = findJobAndVerifyTenant(jobId);
        List<JobApplication> applications = jobApplicationRepository.findByJobPosting(jobPosting);
        return applications.stream().map(this::convertApplicationToDto).collect(Collectors.toList());
    }
    public void updateApplicationStatus(Long applicationId, UpdateApplicationStatusDto statusDto) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        findJobAndVerifyTenant(application.getJobPosting().getJobId());
        try {
            JobApplication.ApplicationStatus newStatus = JobApplication.ApplicationStatus.valueOf(statusDto.getStatus().toUpperCase());
            application.setStatus(newStatus);
            jobApplicationRepository.save(application);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status provided");
        }
    }
    private User getCurrentUser() {
        String mobileNumber = SecurityContextHolder.getContext().getAuthentication().getName();
        String tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByMobileNumberAndTenantId(mobileNumber, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authenticated user not found."));
    }
    private JobPosting findJobAndVerifyTenant(Long jobId) {
        String tenantId = TenantContext.getCurrentTenant();
        JobPosting jobPosting = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (!jobPosting.getTenantId().equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access to this resource is denied");
        }
        return jobPosting;
    }
    private JobPostingDto convertToDto(JobPosting jobPosting) {
        JobPostingDto dto = new JobPostingDto();
        dto.setJobId(jobPosting.getJobId());
        dto.setTitle(jobPosting.getTitle());
        dto.setCompanyName(jobPosting.getCompanyName());
        dto.setLocation(jobPosting.getLocation());
        dto.setDescription(jobPosting.getDescription());
        dto.setStatus(jobPosting.getStatus().name());
        dto.setRejectionRemarks(jobPosting.getRejectionRemarks());
        dto.setHrContactPhone(jobPosting.getHrContactPhone());
        dto.setHrContactEmail(jobPosting.getHrContactEmail());

        
        return dto;
    }
    private ApplicationDto convertApplicationToDto(JobApplication application) {
        ApplicationDto dto = new ApplicationDto();
        dto.setApplicationId(application.getApplicationId());
        dto.setApplicantName(application.getUser().getFullName());
        dto.setApplicantMobile(application.getUser().getMobileNumber());
        dto.setStatus(application.getStatus().name());
        return dto;
    }

	public List<JobPostingDto> getJobsPostedbyMe() {
		
		  User currentUser = getCurrentUser();
	        List<JobPosting> jobPosting = jobPostingRepository.findByAuthorOrderByCreatedAtDesc(currentUser);
	        
	        return jobPosting.stream()
	                .map(this::convertToDto)
	                .collect(Collectors.toList());
		
		
		
		
	}
}

