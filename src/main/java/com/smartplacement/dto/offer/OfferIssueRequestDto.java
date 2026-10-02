package com.smartplacement.dto.offer;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Request payload for issuing an official job/internship offer to a student.
 */
public class OfferIssueRequestDto {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "Offered CTC (LPA) is required")
    @Min(value = 0, message = "CTC cannot be negative")
    private Double ctcLpa;

    @NotBlank(message = "Designation/Role is required")
    @Size(max = 120, message = "Designation cannot exceed 120 characters")
    private String designation;

    @NotNull(message = "Offer validity deadline is required")
    @Future(message = "Offer validity deadline must be a future date")
    private LocalDate validUntil;

    @Future(message = "Expected joining date must be a future date")
    private LocalDate joiningDate;

    private String offerLetterUrl;

    private String notes;

    public OfferIssueRequestDto() {
    }

    public OfferIssueRequestDto(Long applicationId, Double ctcLpa, String designation,
                                LocalDate validUntil, LocalDate joiningDate,
                                String offerLetterUrl, String notes) {
        this.applicationId = applicationId;
        this.ctcLpa = ctcLpa;
        this.designation = designation;
        this.validUntil = validUntil;
        this.joiningDate = joiningDate;
        this.offerLetterUrl = offerLetterUrl;
        this.notes = notes;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
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

    public String getOfferLetterUrl() {
        return offerLetterUrl;
    }

    public void setOfferLetterUrl(String offerLetterUrl) {
        this.offerLetterUrl = offerLetterUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
