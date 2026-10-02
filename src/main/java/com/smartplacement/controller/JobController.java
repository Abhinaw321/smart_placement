package com.smartplacement.controller;

import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.job.JobCreateRequestDto;
import com.smartplacement.dto.job.JobResponseDto;
import com.smartplacement.dto.job.JobUpdateRequestDto;
import com.smartplacement.dto.job.StudentEligibilityCheckResponseDto;
import com.smartplacement.dto.student.StudentProfileResponseDto;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller managing campus placement job openings and rule-based candidate eligibility.
 */
@RestController
@RequestMapping("/api/v1/jobs")
@Tag(name = "Jobs & Eligibility Engine", description = "Endpoints for managing job postings and real-time candidate eligibility checks")
@SecurityRequirement(name = "BearerAuth")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "Create a new job posting with eligibility rules (Recruiter/TPO)")
    public ResponseEntity<ApiResponse<JobResponseDto>> createJob(
            @Valid @RequestBody JobCreateRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        JobResponseDto createdJob = jobService.createJob(request, currentUser);
        return new ResponseEntity<>(
                ApiResponse.success("Job opening published successfully", createdJob),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "Update an existing job opening and its eligibility rules (Recruiter/TPO)")
    public ResponseEntity<ApiResponse<JobResponseDto>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobUpdateRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        JobResponseDto updatedJob = jobService.updateJob(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Job posting updated successfully", updatedJob));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get detailed information about a job posting including criteria")
    public ResponseEntity<ApiResponse<JobResponseDto>> getJobById(@PathVariable Long id) {
        JobResponseDto job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.success("Job details retrieved successfully", job));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search and filter job postings with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<JobResponseDto>>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) JobStatus status,
            @RequestParam(required = false) JobType jobType,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PagedResponse<JobResponseDto> jobs = jobService.getAllJobs(page, size, status, jobType, companyId, search, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Jobs retrieved successfully", jobs));
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all jobs posted by a specific company")
    public ResponseEntity<ApiResponse<PagedResponse<JobResponseDto>>> getJobsByCompany(
            @PathVariable Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<JobResponseDto> jobs = jobService.getJobsByCompany(companyId, page, size);
        return ResponseEntity.ok(ApiResponse.success("Company jobs retrieved successfully", jobs));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "Transition job lifecycle status (e.g. PUBLISHED to CLOSED)")
    public ResponseEntity<ApiResponse<Void>> updateJobStatus(
            @PathVariable Long id,
            @RequestParam JobStatus status,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        jobService.updateJobStatus(id, status, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Job status updated successfully to " + status, null));
    }

    @GetMapping("/{id}/my-eligibility")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Evaluate authenticated student's real-time eligibility with detailed criteria breakdown")
    public ResponseEntity<ApiResponse<StudentEligibilityCheckResponseDto>> checkMyEligibility(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        StudentEligibilityCheckResponseDto evaluation = jobService.checkStudentEligibility(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Candidate eligibility evaluated successfully", evaluation));
    }

    @GetMapping("/{id}/eligible-candidates")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "View all registered students eligible for this job drive (Recruiter/TPO)")
    public ResponseEntity<ApiResponse<List<StudentProfileResponseDto>>> getEligibleCandidates(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<StudentProfileResponseDto> candidates = jobService.getEligibleStudentsForJob(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Eligible candidate pool retrieved successfully", candidates));
    }
}
