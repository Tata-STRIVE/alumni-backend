package com.striveconnect.controller;

import com.striveconnect.dto.ApplicationDto;
import com.striveconnect.dto.CreateJobRequestDto;
import com.striveconnect.dto.JobApplicationDto;
import com.striveconnect.dto.JobPostingDto;
import com.striveconnect.dto.JobReviewDto;
import com.striveconnect.dto.UpdateApplicationStatusDto;
import com.striveconnect.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<JobPostingDto>> getOpenJobs() {
        List<JobPostingDto> jobs = jobService.getOpenJobsForCurrentTenant();
        return ResponseEntity.ok(jobs);
    }
    
    @PostMapping("/{jobId}/apply")
    public ResponseEntity<?> applyToJob(@PathVariable Long jobId) {
        jobService.applyToJob(jobId);
        return ResponseEntity.ok(Map.of("message", "Application submitted successfully"));
    }

    //  ALUMNUS JOB SUBMISSION ---
    @PostMapping("/submit")
    public ResponseEntity<JobPostingDto> submitJobForVerification(@RequestBody CreateJobRequestDto createDto) {
        JobPostingDto submittedJob = jobService.submitJobForVerification(createDto);
        return new ResponseEntity<>(submittedJob, HttpStatus.CREATED);
    }
    
    // NEW: Endpoint for an alumnus to get their job applications
    @GetMapping("/my-applications")
    public ResponseEntity<List<JobApplicationDto>> getMyApplications() {
        List<JobApplicationDto> applications = jobService.getMyJobApplications();
        return ResponseEntity.ok(applications);
    }

    // --- ADMIN ENDPOINTS ---
    @PostMapping
    public ResponseEntity<JobPostingDto> createJob(@RequestBody CreateJobRequestDto createDto) {
        JobPostingDto createdJob = jobService.createJobPosting(createDto);
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }

    @GetMapping("/{jobId}/applications")
    public ResponseEntity<List<ApplicationDto>> getJobApplications(@PathVariable Long jobId) {
        List<ApplicationDto> applications = jobService.getApplicationsForJob(jobId);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<?> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody UpdateApplicationStatusDto statusDto) {
    	System.out.println("updateApplicationStatus");
        jobService.updateApplicationStatus(applicationId, statusDto);
        return ResponseEntity.ok(Map.of("message", "Application status updated successfully"));
    }

    // --- NEW: ADMIN JOB VERIFICATION ---
    @GetMapping("/pending")
    public ResponseEntity<List<JobPostingDto>> getPendingJobs() {
        List<JobPostingDto> pendingJobs = jobService.getPendingJobs();
        return ResponseEntity.ok(pendingJobs);
    }

    @PostMapping("/{jobId}/review")
    public ResponseEntity<?> approveJob(@PathVariable Long jobId ,   @RequestBody JobReviewDto jobReviewDto ) {
      
        jobService.reviewJob(jobId,jobReviewDto);
        return ResponseEntity.ok(Map.of("message", "Job approved successfully."));
    }
    
   
}

