package com.smartplacement.controller;

import com.smartplacement.dto.auth.AuthResponseDto;
import com.smartplacement.dto.auth.LoginRequestDto;
import com.smartplacement.dto.auth.RecruiterRegisterRequestDto;
import com.smartplacement.dto.auth.StudentRegisterRequestDto;
import com.smartplacement.dto.auth.UserSummaryDto;
import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for authentication and identity operations.
 *
 * Exposes endpoints for:
 * - Student account registration
 * - Recruiter account registration
 * - JWT authentication / login
 * - Authenticated user identity retrieval (/me)
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication, registration, and token management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials and receive JWT Bearer token")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Authentication successful", response));
    }

    @PostMapping("/student/register")
    @Operation(summary = "Register a new student account")
    public ResponseEntity<ApiResponse<AuthResponseDto>> registerStudent(@Valid @RequestBody StudentRegisterRequestDto request) {
        AuthResponseDto response = authService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Student account registered successfully", response));
    }

    @PostMapping("/recruiter/register")
    @Operation(summary = "Register a new recruiter account")
    public ResponseEntity<ApiResponse<AuthResponseDto>> registerRecruiter(@Valid @RequestBody RecruiterRegisterRequestDto request) {
        AuthResponseDto response = authService.registerRecruiter(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recruiter account registered successfully", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Retrieve currently authenticated user profile")
    public ResponseEntity<ApiResponse<UserSummaryDto>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal principal) {
        UserSummaryDto userSummary = authService.getCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", userSummary));
    }
}
