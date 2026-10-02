package com.smartplacement.controller;

import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.interview.InterviewResponseDto;
import com.smartplacement.dto.interview.InterviewResultDto;
import com.smartplacement.dto.interview.ScheduleInterviewRequestDto;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.InterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
 * REST controller managing interview scheduling, round evaluations, ratings, and feedback.
 */
@RestController
@RequestMapping("/api/v1/interviews")
@Tag(name = "Assessments & Interviews", description = "Endpoints for scheduling rounds, recording feedback, and tracking interview schedules")
@SecurityRequirement(name = "BearerAuth")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PostMapping("/schedule")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "Schedule an interview round for a shortlisted applicant")
    public ResponseEntity<ApiResponse<InterviewResponseDto>> scheduleInterview(
            @Valid @RequestBody ScheduleInterviewRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        InterviewResponseDto interview = interviewService.scheduleInterview(request, currentUser);
        return new ResponseEntity<>(
                ApiResponse.success("Interview round scheduled successfully", interview),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}/result")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "Submit round evaluation feedback, rating, and pass/fail decision")
    public ResponseEntity<ApiResponse<InterviewResponseDto>> submitInterviewResult(
            @PathVariable Long id,
            @Valid @RequestBody InterviewResultDto request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        InterviewResponseDto result = interviewService.submitInterviewResult(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Interview evaluation recorded successfully", result));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "View logged-in student's upcoming and historical interview rounds")
    public ResponseEntity<ApiResponse<PagedResponse<InterviewResponseDto>>> getMyInterviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PagedResponse<InterviewResponseDto> interviews = interviewService.getMyInterviews(currentUser, page, size);
        return ResponseEntity.ok(ApiResponse.success("Interview rounds retrieved successfully", interviews));
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all interview rounds recorded for a specific application")
    public ResponseEntity<ApiResponse<List<InterviewResponseDto>>> getInterviewsByApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<InterviewResponseDto> list = interviewService.getInterviewsByApplication(applicationId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Interview rounds retrieved successfully", list));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "View scheduled interview slots for a specific job drive (Recruiter/TPO)")
    public ResponseEntity<ApiResponse<PagedResponse<InterviewResponseDto>>> getInterviewsByJob(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PagedResponse<InterviewResponseDto> interviews = interviewService.getInterviewsByJob(jobId, page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Job interviews retrieved successfully", interviews));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get detailed information for a specific interview round")
    public ResponseEntity<ApiResponse<InterviewResponseDto>> getInterviewById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        InterviewResponseDto interview = interviewService.getInterviewById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Interview details retrieved successfully", interview));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "Cancel an interview round")
    public ResponseEntity<ApiResponse<Void>> cancelInterview(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Cancelled by hiring team") String reason,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        interviewService.cancelInterview(id, reason, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Interview cancelled successfully", null));
    }
}
