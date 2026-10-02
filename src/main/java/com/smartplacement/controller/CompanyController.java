package com.smartplacement.controller;

import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.company.CompanyRequestDto;
import com.smartplacement.dto.company.CompanyResponseDto;
import com.smartplacement.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing recruiting partner companies.
 */
@RestController
@RequestMapping("/api/v1/companies")
@Tag(name = "Companies", description = "Endpoints for hiring partner company management and directory browsing")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('TPO_ADMIN', 'RECRUITER')")
    @Operation(summary = "Register new hiring company")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> createCompany(
            @Valid @RequestBody CompanyRequestDto request) {
        CompanyResponseDto created = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company registered successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO_ADMIN', 'RECRUITER')")
    @Operation(summary = "Update existing company details")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequestDto request) {
        CompanyResponseDto updated = companyService.updateCompany(id, request);
        return ResponseEntity.ok(ApiResponse.success("Company updated successfully", updated));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get company details by ID")
    public ResponseEntity<ApiResponse<CompanyResponseDto>> getCompanyById(@PathVariable Long id) {
        CompanyResponseDto company = companyService.getCompanyById(id);
        return ResponseEntity.ok(ApiResponse.success("Company retrieved successfully", company));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Browse and search companies with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<CompanyResponseDto>>> searchCompanies(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        PagedResponse<CompanyResponseDto> result = companyService.searchCompanies(query, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success("Companies retrieved successfully", result));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TPO_ADMIN')")
    @Operation(summary = "Delete company (TPO Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(ApiResponse.success("Company deleted successfully"));
    }
}
