package com.smartplacement.controller;

import com.smartplacement.dto.analytics.RecruiterDashboardDto;
import com.smartplacement.dto.analytics.StudentDashboardDto;
import com.smartplacement.dto.analytics.TpoDashboardDto;
import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller serving role-tailored dashboard analytics and placement report CSV streams.
 */
@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics & Executive Dashboards", description = "Endpoints for role-specific dashboard metrics, KPI aggregations, and CSV exports")
@SecurityRequirement(name = "BearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/student-dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Retrieve personalized placement overview for the logged-in student")
    public ResponseEntity<ApiResponse<StudentDashboardDto>> getStudentDashboard(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        StudentDashboardDto dashboard = analyticsService.getStudentDashboard(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Student dashboard metrics retrieved successfully", dashboard));
    }

    @GetMapping("/recruiter-dashboard")
    @PreAuthorize("hasRole('RECRUITER')")
    @Operation(summary = "Retrieve recruitment drive pipeline throughput for the logged-in recruiter")
    public ResponseEntity<ApiResponse<RecruiterDashboardDto>> getRecruiterDashboard(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        RecruiterDashboardDto dashboard = analyticsService.getRecruiterDashboard(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Recruiter dashboard metrics retrieved successfully", dashboard));
    }

    @GetMapping("/tpo-dashboard")
    @PreAuthorize("hasRole('TPO_ADMIN')")
    @Operation(summary = "Retrieve institutional placement statistics, branch aggregations, and salary tiers")
    public ResponseEntity<ApiResponse<TpoDashboardDto>> getTpoDashboard() {
        TpoDashboardDto dashboard = analyticsService.getTpoDashboard();
        return ResponseEntity.ok(ApiResponse.success("TPO executive dashboard retrieved successfully", dashboard));
    }

    @GetMapping("/export/placements")
    @PreAuthorize("hasRole('TPO_ADMIN')")
    @Operation(summary = "Download institutional placement master sheet as RFC 4180 CSV")
    public ResponseEntity<byte[]> exportPlacementsCsv() {
        byte[] csvData = analyticsService.exportPlacementsCsv();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"placements_master_report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(csvData.length)
                .body(csvData);
    }
}
