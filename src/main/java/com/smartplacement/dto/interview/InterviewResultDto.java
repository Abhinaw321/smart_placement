package com.smartplacement.dto.interview;

import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.entity.RoundResult;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for submitting evaluator feedback, numerical rating, and round decision.
 */
public class InterviewResultDto {

    @NotNull(message = "Round result is required (CLEARED, REJECTED, ON_HOLD)")
    private RoundResult result;

    @NotBlank(message = "Evaluator feedback is required")
    private String feedback;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    private ApplicationStatus advanceApplicationStatus;

    private String nextRoundName;

    public InterviewResultDto() {
    }

    public InterviewResultDto(RoundResult result, String feedback, Integer rating,
                              ApplicationStatus advanceApplicationStatus, String nextRoundName) {
        this.result = result;
        this.feedback = feedback;
        this.rating = rating;
        this.advanceApplicationStatus = advanceApplicationStatus;
        this.nextRoundName = nextRoundName;
    }

    public RoundResult getResult() {
        return result;
    }

    public void setResult(RoundResult result) {
        this.result = result;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public ApplicationStatus getAdvanceApplicationStatus() {
        return advanceApplicationStatus;
    }

    public void setAdvanceApplicationStatus(ApplicationStatus advanceApplicationStatus) {
        this.advanceApplicationStatus = advanceApplicationStatus;
    }

    public String getNextRoundName() {
        return nextRoundName;
    }

    public void setNextRoundName(String nextRoundName) {
        this.nextRoundName = nextRoundName;
    }
}
