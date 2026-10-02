package com.smartplacement.dto.interview;

import com.smartplacement.entity.Interview;
import com.smartplacement.entity.InterviewStatus;
import com.smartplacement.entity.InterviewType;
import com.smartplacement.entity.RoundResult;

import java.time.LocalDateTime;

/**
 * Detailed representation of an interview round for candidates and recruiters.
 */
public class InterviewResponseDto {

    private Long id;
    private Long applicationId;
    private Long studentId;
    private String studentRollNumber;
    private String studentName;
    private String studentEmail;

    private Long jobId;
    private String jobTitle;
    private Long companyId;
    private String companyName;
    private String companyLogoUrl;

    private Integer roundNumber;
    private String roundName;
    private InterviewType interviewType;
    private LocalDateTime scheduledAt;
    private String meetingLinkOrVenue;
    private String interviewerName;
    private String interviewerEmail;

    private InterviewStatus status;
    private RoundResult result;
    private String feedback;
    private Integer rating;
    private LocalDateTime conductedAt;
    private LocalDateTime createdAt;

    public InterviewResponseDto() {
    }

    public static InterviewResponseDto fromEntity(Interview interview) {
        InterviewResponseDto dto = new InterviewResponseDto();
        dto.setId(interview.getId());
        dto.setRoundNumber(interview.getRoundNumber());
        dto.setRoundName(interview.getRoundName());
        dto.setInterviewType(interview.getInterviewType());
        dto.setScheduledAt(interview.getScheduledAt());
        dto.setMeetingLinkOrVenue(interview.getMeetingLinkOrVenue());
        dto.setInterviewerName(interview.getInterviewerName());
        dto.setInterviewerEmail(interview.getInterviewerEmail());
        dto.setStatus(interview.getStatus());
        dto.setResult(interview.getResult());
        dto.setFeedback(interview.getFeedback());
        dto.setRating(interview.getRating());
        dto.setConductedAt(interview.getConductedAt());
        dto.setCreatedAt(interview.getCreatedAt());

        if (interview.getApplication() != null) {
            dto.setApplicationId(interview.getApplication().getId());

            if (interview.getApplication().getStudent() != null) {
                dto.setStudentId(interview.getApplication().getStudent().getId());
                dto.setStudentRollNumber(interview.getApplication().getStudent().getRollNumber());
                dto.setStudentName(interview.getApplication().getStudent().getFirstName() + " "
                        + interview.getApplication().getStudent().getLastName());

                if (interview.getApplication().getStudent().getUser() != null) {
                    dto.setStudentEmail(interview.getApplication().getStudent().getUser().getEmail());
                }
            }

            if (interview.getApplication().getJob() != null) {
                dto.setJobId(interview.getApplication().getJob().getId());
                dto.setJobTitle(interview.getApplication().getJob().getTitle());

                if (interview.getApplication().getJob().getCompany() != null) {
                    dto.setCompanyId(interview.getApplication().getJob().getCompany().getId());
                    dto.setCompanyName(interview.getApplication().getJob().getCompany().getName());
                    dto.setCompanyLogoUrl(interview.getApplication().getJob().getCompany().getLogoUrl());
                }
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

    public InterviewStatus getStatus() {
        return status;
    }

    public void setStatus(InterviewStatus status) {
        this.status = status;
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

    public LocalDateTime getConductedAt() {
        return conductedAt;
    }

    public void setConductedAt(LocalDateTime conductedAt) {
        this.conductedAt = conductedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
