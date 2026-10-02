package com.smartplacement.dto.job;

import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Request payload for creating a new campus placement job opening.
 */
public class JobCreateRequestDto {

    private Long companyId;

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 150, message = "Job title must be between 3 and 150 characters")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotNull(message = "Job type is required (FULL_TIME, INTERNSHIP, INTERN_PLUS_FULL_TIME)")
    private JobType jobType = JobType.FULL_TIME;

    @Size(max = 150, message = "Location cannot exceed 150 characters")
    private String location;

    @NotNull(message = "Salary package (LPA) is required")
    @DecimalMin(value = "0.0", message = "Salary package cannot be negative")
    private Double salaryPackageLpa;

    @NotNull(message = "Application deadline is required")
    @Future(message = "Application deadline must be a future date/time")
    private LocalDateTime applicationDeadline;

    private LocalDate driveDate;

    private JobStatus status = JobStatus.PUBLISHED;

    @NotNull(message = "Eligibility criteria must be configured")
    @Valid
    private EligibilityCriteriaDto eligibilityCriteria;

    public JobCreateRequestDto() {
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public EligibilityCriteriaDto getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    public void setEligibilityCriteria(EligibilityCriteriaDto eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }
}
