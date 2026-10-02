package com.smartplacement.controller;

import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.student.StudentAcademicUpdateDto;
import com.smartplacement.dto.student.StudentCertificationDto;
import com.smartplacement.dto.student.StudentPersonalInfoUpdateDto;
import com.smartplacement.dto.student.StudentProfileResponseDto;
import com.smartplacement.dto.student.StudentProjectDto;
import com.smartplacement.dto.student.StudentSkillDto;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for student profile management, portfolio items, and resume uploads.
 */
@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Student Profile", description = "Endpoints for student profile management, resume upload, skills, and projects")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Retrieve complete profile for currently authenticated student")
    public ResponseEntity<ApiResponse<StudentProfileResponseDto>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        StudentProfileResponseDto profile = studentService.getMyProfile(principal);
        return ResponseEntity.ok(ApiResponse.success("Student profile retrieved", profile));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Update personal contact details")
    public ResponseEntity<ApiResponse<StudentProfileResponseDto>> updatePersonalInfo(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StudentPersonalInfoUpdateDto request) {
        StudentProfileResponseDto updated = studentService.updateMyPersonalInfo(principal, request);
        return ResponseEntity.ok(ApiResponse.success("Personal information updated successfully", updated));
    }

    @PutMapping("/me/academic")
    @PreAuthorize("hasAnyRole('STUDENT', 'TPO_ADMIN')")
    @Operation(summary = "Update academic records (roll number, branch, CGPA, backlogs, marks)")
    public ResponseEntity<ApiResponse<StudentProfileResponseDto>> updateAcademicInfo(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StudentAcademicUpdateDto request) {
        StudentProfileResponseDto updated = studentService.updateMyAcademicInfo(principal, request);
        return ResponseEntity.ok(ApiResponse.success("Academic information updated successfully", updated));
    }

    @PostMapping(value = "/me/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Upload primary PDF resume document (max 5MB)")
    public ResponseEntity<ApiResponse<StudentProfileResponseDto>> uploadResume(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file) {
        StudentProfileResponseDto profile = studentService.uploadResume(principal, file);
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully", profile));
    }

    @GetMapping("/me/resume")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Stream or download student's own active resume PDF")
    public ResponseEntity<Resource> downloadMyResume(
            @AuthenticationPrincipal UserPrincipal principal) {
        Resource resumeResource = studentService.downloadMyResume(principal);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"my_resume.pdf\"")
                .body(resumeResource);
    }

    @PostMapping("/me/skills")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Add technical skill to student profile")
    public ResponseEntity<ApiResponse<StudentSkillDto>> addSkill(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StudentSkillDto request) {
        StudentSkillDto skill = studentService.addSkill(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Skill added successfully", skill));
    }

    @DeleteMapping("/me/skills/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Remove skill from profile")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        studentService.deleteSkill(principal, id);
        return ResponseEntity.ok(ApiResponse.success("Skill removed successfully"));
    }

    @PostMapping("/me/projects")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Add portfolio project")
    public ResponseEntity<ApiResponse<StudentProjectDto>> addProject(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StudentProjectDto request) {
        StudentProjectDto project = studentService.addProject(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project added successfully", project));
    }

    @DeleteMapping("/me/projects/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Delete portfolio project")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        studentService.deleteProject(principal, id);
        return ResponseEntity.ok(ApiResponse.success("Project removed successfully"));
    }

    @PostMapping("/me/certifications")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Add certification")
    public ResponseEntity<ApiResponse<StudentCertificationDto>> addCertification(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StudentCertificationDto request) {
        StudentCertificationDto cert = studentService.addCertification(principal, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Certification added successfully", cert));
    }

    @DeleteMapping("/me/certifications/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Delete certification")
    public ResponseEntity<ApiResponse<Void>> deleteCertification(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        studentService.deleteCertification(principal, id);
        return ResponseEntity.ok(ApiResponse.success("Certification removed successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('TPO_ADMIN')")
    @Operation(summary = "Search and filter students with pagination (TPO Admin only)")
    public ResponseEntity<ApiResponse<PagedResponse<StudentProfileResponseDto>>> searchStudents(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) Integer gradYear,
            @RequestParam(required = false) Double minCgpa,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cgpa") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PagedResponse<StudentProfileResponseDto> result = studentService.searchStudents(
                branch, gradYear, minCgpa, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TPO_ADMIN', 'RECRUITER', 'STUDENT')")
    @Operation(summary = "Retrieve specific student profile by ID")
    public ResponseEntity<ApiResponse<StudentProfileResponseDto>> getStudentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        StudentProfileResponseDto profile = studentService.getStudentById(id, principal);
        return ResponseEntity.ok(ApiResponse.success("Student profile retrieved", profile));
    }

    @GetMapping("/{id}/resume")
    @PreAuthorize("hasAnyRole('TPO_ADMIN', 'RECRUITER', 'STUDENT')")
    @Operation(summary = "Download student resume PDF by authorized users")
    public ResponseEntity<Resource> downloadStudentResume(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        Resource resource = studentService.downloadStudentResume(id, principal);
        String filename = studentService.getResumeFilename(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
