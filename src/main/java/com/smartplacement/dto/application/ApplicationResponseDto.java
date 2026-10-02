package com.smartplacement.dto.application;

import com.smartplacement.entity.Application;
import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.entity.JobType;

import java.time.LocalDateTime;

/**
 * Detailed representation of a student application in the recruitment pipeline.
 */
public class ApplicationResponseDto {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private JobType jobType;
    private Long companyId;
    private String companyName;
    private String companyLogoUrl;
    private Double salaryPackageLpa;

    private Long studentId;
    private String studentRollNumber;
    private String studentName;
    private String studentEmail;
    private String studentBranch;
    private Double studentCgpa;

    private ApplicationStatus status;
    private String currentRound;
    private String resumeSnapshotUrl;
    private String rejectionReason;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    public ApplicationResponseDto() {
    }

    public static ApplicationResponseDto fromEntity(Application application) {
        ApplicationResponseDto dto = new ApplicationResponseDto();
        dto.setId(application.getId());

        if (application.getJob() != null) {
            dto.setJobId(application.getJob().getId());
            dto.setJobTitle(application.getJob().getTitle());
            dto.setJobType(application.getJob().getJobType());
            dto.setSalaryPackageLpa(application.getJob().getSalaryPackageLpa());

            if (application.getJob().getCompany() != null) {
                dto.setCompanyId(application.getJob().getCompany().getId());
                dto.setCompanyName(application.getJob().getCompany().getName());
                dto.setCompanyLogoUrl(application.getJob().getCompany().getLogoUrl());
            }
        }

        if (application.getStudent() != null) {
            dto.setStudentId(application.getStudent().getId());
            dto.setStudentRollNumber(application.getStudent().getRollNumber());
            dto.setStudentName(application.getStudent().getFirstName() + " " + application.getStudent().getLastName());
            dto.setStudentBranch(application.getStudent().getBranch());
            dto.setStudentCgpa(application.getStudent().getCgpa());

            if (application.getStudent().getUser() != null) {
                dto.setStudentEmail(application.getStudent().getUser().getEmail());
            }
        }

        dto.setStatus(application.getStatus());
        dto.setCurrentRound(application.getCurrentRound());
        dto.setResumeSnapshotUrl(application.getResumeSnapshotUrl());
        dto.setRejectionReason(application.getRejectionReason());
        dto.setAppliedAt(application.getAppliedAt());
        dto.setUpdatedAt(application.getUpdatedAt());

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public void setCompanyLogoUrl(String companyLogoUrl) {
        this.companyLogoUrl = companyLogoUrl;
    }

    public Double getSalaryPackageLpa() {
        return salaryPackageLpa;
    }

    public void setSalaryPackageLpa(Double salaryPackageLpa) {
        this.salaryPackageLpa = salaryPackageLpa;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentBranch() {
        return studentBranch;
    }

    public void setStudentBranch(String studentBranch) {
        this.studentBranch = studentBranch;
    }

    public Double getStudentCgpa() {
        return studentCgpa;
    }

    public void setStudentCgpa(Double studentCgpa) {
        this.studentCgpa = studentCgpa;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(String currentRound) {
        this.currentRound = currentRound;
    }

    public String getResumeSnapshotUrl() {
        return resumeSnapshotUrl;
    }

    public void setResumeSnapshotUrl(String resumeSnapshotUrl) {
        this.resumeSnapshotUrl = resumeSnapshotUrl;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
