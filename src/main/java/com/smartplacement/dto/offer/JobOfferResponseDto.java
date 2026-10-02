package com.smartplacement.dto.offer;

import com.smartplacement.entity.JobOffer;
import com.smartplacement.entity.OfferStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Detailed representation of a formal job offer for student, recruiter, and administrator views.
 */
public class JobOfferResponseDto {

    private Long id;
    private Long applicationId;
    private Long studentId;
    private String studentRollNumber;
    private String studentName;
    private String studentEmail;
    private String studentBranch;

    private Long jobId;
    private String jobTitle;
    private Long companyId;
    private String companyName;
    private String companyLogoUrl;

    private Double ctcLpa;
    private String designation;
    private String offerLetterUrl;
    private LocalDate issueDate;
    private LocalDate validUntil;
    private LocalDate joiningDate;

    private OfferStatus status;
    private LocalDateTime responseDate;
    private String studentRemarks;
    private String notes;
    private LocalDateTime createdAt;

    public JobOfferResponseDto() {
    }

    public static JobOfferResponseDto fromEntity(JobOffer offer) {
        JobOfferResponseDto dto = new JobOfferResponseDto();
        dto.setId(offer.getId());
        dto.setCtcLpa(offer.getCtcLpa());
        dto.setDesignation(offer.getDesignation());
        dto.setOfferLetterUrl(offer.getOfferLetterUrl());
        dto.setIssueDate(offer.getIssueDate());
        dto.setValidUntil(offer.getValidUntil());
        dto.setJoiningDate(offer.getJoiningDate());
        dto.setStatus(offer.getStatus());
        dto.setResponseDate(offer.getResponseDate());
        dto.setStudentRemarks(offer.getStudentRemarks());
        dto.setNotes(offer.getNotes());
        dto.setCreatedAt(offer.getCreatedAt());

        if (offer.getApplication() != null) {
            dto.setApplicationId(offer.getApplication().getId());
        }

        if (offer.getStudent() != null) {
            dto.setStudentId(offer.getStudent().getId());
            dto.setStudentRollNumber(offer.getStudent().getRollNumber());
            dto.setStudentName(offer.getStudent().getFirstName() + " " + offer.getStudent().getLastName());
            dto.setStudentBranch(offer.getStudent().getBranch());
            if (offer.getStudent().getUser() != null) {
                dto.setStudentEmail(offer.getStudent().getUser().getEmail());
            }
        }

        if (offer.getJob() != null) {
            dto.setJobId(offer.getJob().getId());
            dto.setJobTitle(offer.getJob().getTitle());
            if (offer.getJob().getCompany() != null) {
                dto.setCompanyId(offer.getJob().getCompany().getId());
                dto.setCompanyName(offer.getJob().getCompany().getName());
                dto.setCompanyLogoUrl(offer.getJob().getCompany().getLogoUrl());
            }
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
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

    public Double getCtcLpa() {
        return ctcLpa;
    }

    public void setCtcLpa(Double ctcLpa) {
        this.ctcLpa = ctcLpa;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getOfferLetterUrl() {
        return offerLetterUrl;
    }

    public void setOfferLetterUrl(String offerLetterUrl) {
        this.offerLetterUrl = offerLetterUrl;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public String getStudentRemarks() {
        return studentRemarks;
    }

    public void setStudentRemarks(String studentRemarks) {
        this.studentRemarks = studentRemarks;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
