package com.smartplacement.controller;

import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.dto.company.RecruiterProfileResponseDto;
import com.smartplacement.dto.company.RecruiterUpdateDto;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.RecruiterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for recruiter profiles and company affiliation.
 */
@RestController
@RequestMapping("/api/v1/recruiters")
@Tag(name = "Recruiters", description = "Endpoints for corporate recruiter profiles and company team management")
public class RecruiterController {

    private final RecruiterService recruiterService;

    public RecruiterController(RecruiterService recruiterService) {
        this.recruiterService = recruiterService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('RECRUITER')")
    @Operation(summary = "Retrieve currently authenticated recruiter profile and company details")
    public ResponseEntity<ApiResponse<RecruiterProfileResponseDto>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        RecruiterProfileResponseDto profile = recruiterService.getMyProfile(principal);
        return ResponseEntity.ok(ApiResponse.success("Recruiter profile retrieved", profile));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('RECRUITER')")
    @Operation(summary = "Update recruiter designation and contact phone")
    public ResponseEntity<ApiResponse<RecruiterProfileResponseDto>> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RecruiterUpdateDto request) {
        RecruiterProfileResponseDto updated = recruiterService.updateMyProfile(principal, request);
        return ResponseEntity.ok(ApiResponse.success("Recruiter profile updated successfully", updated));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO_ADMIN', 'RECRUITER')")
    @Operation(summary = "Get recruiter details by ID")
    public ResponseEntity<ApiResponse<RecruiterProfileResponseDto>> getRecruiterById(@PathVariable Long id) {
        RecruiterProfileResponseDto recruiter = recruiterService.getRecruiterById(id);
        return ResponseEntity.ok(ApiResponse.success("Recruiter details retrieved", recruiter));
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('TPO_ADMIN', 'RECRUITER')")
    @Operation(summary = "List all recruiters associated with a specific company")
    public ResponseEntity<ApiResponse<List<RecruiterProfileResponseDto>>> getRecruitersByCompany(
            @PathVariable Long companyId) {
        List<RecruiterProfileResponseDto> recruiters = recruiterService.getRecruitersByCompany(companyId);
        return ResponseEntity.ok(ApiResponse.success("Recruiters retrieved successfully", recruiters));
    }
}
