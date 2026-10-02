package com.smartplacement.dto.application;

import com.smartplacement.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for transitioning an application's lifecycle status.
 */
public class ApplicationStatusUpdateDto {

    @NotNull(message = "Target application status is required")
    private ApplicationStatus status;

    @Size(max = 100, message = "Round name cannot exceed 100 characters")
    private String currentRound;

    private String rejectionReason;

    private String notes;

    public ApplicationStatusUpdateDto() {
    }

    public ApplicationStatusUpdateDto(ApplicationStatus status, String currentRound, String rejectionReason) {
        this.status = status;
        this.currentRound = currentRound;
        this.rejectionReason = rejectionReason;
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

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
