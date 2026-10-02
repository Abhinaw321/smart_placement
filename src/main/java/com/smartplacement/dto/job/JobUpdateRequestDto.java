package com.smartplacement.dto.job;

import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Request payload for updating an existing job opening and its eligibility criteria.
 */
public class JobUpdateRequestDto {

    @Size(min = 3, max = 150, message = "Job title must be between 3 and 150 characters")
    private String title;

    private String description;

    private JobType jobType;

    @Size(max = 150, message = "Location cannot exceed 150 characters")
    private String location;

    @DecimalMin(value = "0.0", message = "Salary package cannot be negative")
    private Double salaryPackageLpa;

    private LocalDateTime applicationDeadline;

    private LocalDate driveDate;

    private JobStatus status;

    @Valid
    private EligibilityCriteriaDto eligibilityCriteria;

    public JobUpdateRequestDto() {
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
