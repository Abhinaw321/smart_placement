package com.smartplacement.dto.interview;

import com.smartplacement.entity.InterviewType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Request payload for scheduling an assessment or interview round with a candidate.
 */
public class ScheduleInterviewRequestDto {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "Round number is required")
    @Min(value = 1, message = "Round number must be at least 1")
    private Integer roundNumber = 1;

    @NotBlank(message = "Round name is required (e.g. Technical Round 1)")
    @Size(max = 100, message = "Round name cannot exceed 100 characters")
    private String roundName;

    @NotNull(message = "Interview type is required (ONLINE_MEET, OFFLINE_CAMPUS, TELEPHONIC)")
    private InterviewType interviewType = InterviewType.ONLINE_MEET;

    @NotNull(message = "Interview scheduled date/time is required")
    @Future(message = "Interview schedule must be a future timestamp")
    private LocalDateTime scheduledAt;

    @NotBlank(message = "Meeting link or campus venue is required")
    @Size(max = 255, message = "Meeting link or venue cannot exceed 255 characters")
    private String meetingLinkOrVenue;

    @NotBlank(message = "Interviewer name is required")
    @Size(max = 100, message = "Interviewer name cannot exceed 100 characters")
    private String interviewerName;

    @Email(message = "Interviewer email must be a valid email address")
    private String interviewerEmail;

    public ScheduleInterviewRequestDto() {
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public InterviewType getInterviewType() {
        return interviewType;
    }

    public void setInterviewType(InterviewType interviewType) {
        this.interviewType = interviewType;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getMeetingLinkOrVenue() {
        return meetingLinkOrVenue;
    }

    public void setMeetingLinkOrVenue(String meetingLinkOrVenue) {
        this.meetingLinkOrVenue = meetingLinkOrVenue;
    }

    public String getInterviewerName() {
        return interviewerName;
    }

    public void setInterviewerName(String interviewerName) {
        this.interviewerName = interviewerName;
    }

    public String getInterviewerEmail() {
        return interviewerEmail;
    }

    public void setInterviewerEmail(String interviewerEmail) {
        this.interviewerEmail = interviewerEmail;
    }
}
