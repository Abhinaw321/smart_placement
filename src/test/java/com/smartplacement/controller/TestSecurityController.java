package com.smartplacement.controller;

import com.smartplacement.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller strictly for testing Role-Based Access Control (@PreAuthorize) rules.
 */
@RestController
@RequestMapping("/api/v1/test-rbac")
public class TestSecurityController {

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<String>> studentOnly() {
        return ResponseEntity.ok(ApiResponse.success("Success", "Welcome Student"));
    }

    @GetMapping("/recruiter")
    @PreAuthorize("hasRole('RECRUITER')")
    public ResponseEntity<ApiResponse<String>> recruiterOnly() {
        return ResponseEntity.ok(ApiResponse.success("Success", "Welcome Recruiter"));
    }

    @GetMapping("/tpo")
    @PreAuthorize("hasRole('TPO_ADMIN')")
    public ResponseEntity<ApiResponse<String>> tpoOnly() {
        return ResponseEntity.ok(ApiResponse.success("Success", "Welcome TPO Admin"));
    }
}
