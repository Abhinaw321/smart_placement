package com.smartplacement.dto.job;

import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Detailed representation of a campus placement job opening.
 */
public class JobResponseDto {

    private Long id;
    private Long companyId;
    private String companyName;
    private String companyLogoUrl;
    private String title;
    private String description;
    private JobType jobType;
    private String location;
    private Double salaryPackageLpa;
    private LocalDateTime applicationDeadline;
    private LocalDate driveDate;
    private JobStatus status;
    private LocalDateTime createdAt;
    private String createdByRecruiterName;
    private EligibilityCriteriaDto eligibilityCriteria;

    public JobResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JobType getJobType() {
        return jobType;
    }

    public void setJobType(JobType jobType) {
        this.jobType = jobType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getSalaryPackageLpa() {
        return salaryPackageLpa;
    }

    public void setSalaryPackageLpa(Double salaryPackageLpa) {
        this.salaryPackageLpa = salaryPackageLpa;
    }

    public LocalDateTime getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDateTime applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public LocalDate getDriveDate() {
        return driveDate;
    }

    public void setDriveDate(LocalDate driveDate) {
        this.driveDate = driveDate;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByRecruiterName() {
        return createdByRecruiterName;
    }

    public void setCreatedByRecruiterName(String createdByRecruiterName) {
        this.createdByRecruiterName = createdByRecruiterName;
    }

    public EligibilityCriteriaDto getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    public void setEligibilityCriteria(EligibilityCriteriaDto eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }
}
