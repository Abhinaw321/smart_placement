package com.smartplacement.controller;

import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.offer.JobOfferResponseDto;
import com.smartplacement.dto.offer.OfferIssueRequestDto;
import com.smartplacement.dto.offer.OfferResponseRequestDto;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.JobOfferService;
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

/**
 * REST controller managing formal job offers, candidate decisions, revocations, and placement confirmations.
 */
@RestController
@RequestMapping("/api/v1/offers")
@Tag(name = "Job Offers & Placements", description = "Endpoints for extending employment offers, student responses, and revocations")
@SecurityRequirement(name = "BearerAuth")
public class JobOfferController {

    private final JobOfferService jobOfferService;

    public JobOfferController(JobOfferService jobOfferService) {
        this.jobOfferService = jobOfferService;
    }

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "Extend a formal job offer to a selected candidate")
    public ResponseEntity<ApiResponse<JobOfferResponseDto>> issueOffer(
            @Valid @RequestBody OfferIssueRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        JobOfferResponseDto offer = jobOfferService.issueOffer(request, currentUser);
        return new ResponseEntity<>(
                ApiResponse.success("Job offer extended successfully", offer),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}/respond")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Respond to an employment offer (ACCEPT or DECLINE)")
    public ResponseEntity<ApiResponse<JobOfferResponseDto>> respondToOffer(
            @PathVariable Long id,
            @Valid @RequestBody OfferResponseRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        JobOfferResponseDto offer = jobOfferService.respondToOffer(id, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Offer response recorded successfully", offer));
    }

    @PutMapping("/{id}/revoke")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "Revoke an extended job offer before candidate acceptance")
    public ResponseEntity<ApiResponse<JobOfferResponseDto>> revokeOffer(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Revoked by hiring committee") String reason,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        JobOfferResponseDto offer = jobOfferService.revokeOffer(id, reason, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Job offer revoked successfully", offer));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Retrieve all job offers received by the logged-in candidate")
    public ResponseEntity<ApiResponse<PagedResponse<JobOfferResponseDto>>> getMyOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PagedResponse<JobOfferResponseDto> offers = jobOfferService.getMyOffers(currentUser, page, size);
        return ResponseEntity.ok(ApiResponse.success("Offers retrieved successfully", offers));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAnyRole('RECRUITER', 'TPO_ADMIN')")
    @Operation(summary = "View all offers issued for a specific job drive (Recruiter/TPO)")
    public ResponseEntity<ApiResponse<PagedResponse<JobOfferResponseDto>>> getOffersByJob(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PagedResponse<JobOfferResponseDto> offers = jobOfferService.getOffersByJob(jobId, page, size, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Job drive offers retrieved successfully", offers));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get detailed information for a specific job offer")
    public ResponseEntity<ApiResponse<JobOfferResponseDto>> getOfferById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        JobOfferResponseDto offer = jobOfferService.getOfferById(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Job offer details retrieved successfully", offer));
    }
}
