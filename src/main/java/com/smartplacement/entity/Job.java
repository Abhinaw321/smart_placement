package com.smartplacement.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an on-campus employment opening posted by a hiring partner or TPO.
 */
@Entity
@Table(
        name = "jobs",
        indexes = {
                @Index(name = "idx_jobs_company_id", columnList = "company_id"),
                @Index(name = "idx_jobs_status_deadline", columnList = "status, application_deadline")
        }
)
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_recruiter_id")
    private Recruiter createdByRecruiter;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 30)
    private JobType jobType = JobType.FULL_TIME;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "salary_package_lpa", nullable = false)
    private Double salaryPackageLpa = 0.0;

    @Column(name = "application_deadline", nullable = false)
    private LocalDateTime applicationDeadline;

    @Column(name = "drive_date")
    private LocalDate driveDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private JobStatus status = JobStatus.PUBLISHED;

    @OneToOne(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EligibilityCriteria eligibilityCriteria;

    public Job() {
    }

    public Job(Company company, Recruiter createdByRecruiter, String title, String description,
               JobType jobType, String location, Double salaryPackageLpa,
               LocalDateTime applicationDeadline, LocalDate driveDate, JobStatus status) {
        this.company = company;
        this.createdByRecruiter = createdByRecruiter;
        this.title = title;
        this.description = description;
        this.jobType = jobType != null ? jobType : JobType.FULL_TIME;
        this.location = location;
        this.salaryPackageLpa = salaryPackageLpa != null ? salaryPackageLpa : 0.0;
        this.applicationDeadline = applicationDeadline;
        this.driveDate = driveDate;
        this.status = status != null ? status : JobStatus.PUBLISHED;
    }

    public void setEligibilityCriteria(EligibilityCriteria criteria) {
        if (criteria == null) {
            if (this.eligibilityCriteria != null) {
                this.eligibilityCriteria.setJob(null);
            }
        } else {
            criteria.setJob(this);
        }
        this.eligibilityCriteria = criteria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Recruiter getCreatedByRecruiter() {
        return createdByRecruiter;
    }

    public void setCreatedByRecruiter(Recruiter createdByRecruiter) {
        this.createdByRecruiter = createdByRecruiter;
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

    public EligibilityCriteria getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
