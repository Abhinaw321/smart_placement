package com.smartplacement.dto.application;

import com.smartplacement.entity.ApplicationStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Request payload for bulk transitioning multiple candidate applications simultaneously.
 */
public class BatchApplicationStatusUpdateDto {

    @NotEmpty(message = "At least one application ID must be provided")
    private List<Long> applicationIds;

    @NotNull(message = "Target application status is required")
    private ApplicationStatus status;

    @Size(max = 100, message = "Round name cannot exceed 100 characters")
    private String currentRound;

    private String rejectionReason;

    public BatchApplicationStatusUpdateDto() {
    }

    public BatchApplicationStatusUpdateDto(List<Long> applicationIds, ApplicationStatus status, String currentRound, String rejectionReason) {
        this.applicationIds = applicationIds;
        this.status = status;
        this.currentRound = currentRound;
        this.rejectionReason = rejectionReason;
    }

    public List<Long> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(List<Long> applicationIds) {
        this.applicationIds = applicationIds;
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
}
