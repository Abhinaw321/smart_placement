package com.smartplacement.controller;

import com.smartplacement.dto.audit.AuditLogResponseDto;
import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for institutional administrators inspecting the immutable system audit trail.
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Compliance & Audit Logs", description = "Endpoints for inspecting institutional audit logs and state changes")
@SecurityRequirement(name = "BearerAuth")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('TPO_ADMIN')")
    @Operation(summary = "Retrieve paginated immutable audit logs with optional action filtering (TPO Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLogResponseDto>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String action) {
        PagedResponse<AuditLogResponseDto> logs = auditLogService.getAuditLogs(page, size, action);
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved successfully", logs));
    }
}
