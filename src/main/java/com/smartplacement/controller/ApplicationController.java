package com.smartplacement.controller;

import com.smartplacement.dto.application.ApplicationResponseDto;
import com.smartplacement.dto.application.ApplicationStatusUpdateDto;
import com.smartplacement.dto.application.BatchApplicationStatusUpdateDto;
import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.ApplicationWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller managing campus placement job applications, state machine transitions,
 * candidate pipeline queries, and application withdrawals.
 */
@RestController
@RequestMapping("/api/v1/applications")
@Tag(name = "Applications & Recruitment Workflow", description = "Endpoints for applying to jobs, reviewing pipelines, and advancing candidates")
@SecurityRequirement(name = "BearerAuth")
public class ApplicationController {

    private final ApplicationWorkflowService applicationWorkflowService;

    public ApplicationController(ApplicationWorkflowService applicationWorkflowService) {
        this.applicationWorkflowService = applicationWorkflowService;
    }

    @PostMapping("/jobs/{jobId}/apply")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit a formal job application (verifies eligibility, resume, and deadline)")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> applyForJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ApplicationResponseDto application = applicationWorkflowService.applyForJob(jobId, currentUser);
        return new ResponseEntity<>(
                ApiResponse.success("Job application submitted successfully", application),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View logged-in student's personal application history and current round statuses")
    public ResponseEntity<ApiResponse<PagedResponse<ApplicationResponseDto>>> getMyApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ApplicationStatus status,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PagedResponse<ApplicationResponseDto> applications = applicationWorkflowService.getMyApplications(currentUser, page, size, status);
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved successfully", applications));
    }

    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "View applicant pipeline for a specific job drive (Recruiter/TPO)")
    public ResponseEntity<ApiResponse<PagedResponse<ApplicationResponseDto>>> getJobApplications(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ApplicationStatus status,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PagedResponse<ApplicationResponseDto> applications = applicationWorkflowService.getJobApplications(jobId, status, page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Candidate pipeline retrieved successfully", applications));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get detailed view of a specific application")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> getApplicationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ApplicationResponseDto application = applicationWorkflowService.getApplicationById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Application details retrieved successfully", application));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "Transition application status (e.g. SHORTLISTED, TECHNICAL_INTERVIEW, REJECTED)")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateDto request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        ApplicationResponseDto updated = applicationWorkflowService.updateApplicationStatus(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Application status updated to " + updated.getStatus(), updated));
    }

    @PostMapping("/batch-status")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "Bulk transition multiple candidate applications in one operation (Recruiter/TPO)")
    public ResponseEntity<ApiResponse<List<ApplicationResponseDto>>> batchUpdateApplicationStatus(
            @Valid @RequestBody BatchApplicationStatusUpdateDto request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<ApplicationResponseDto> updatedList = applicationWorkflowService.batchUpdateApplicationStatus(request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Batch updated " + updatedList.size() + " applications successfully", updatedList));
    }

    @DeleteMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Withdraw submitted application (only permissible while status is APPLIED)")
    public ResponseEntity<ApiResponse<Void>> withdrawApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        applicationWorkflowService.withdrawApplication(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn successfully", null));
    }
}
