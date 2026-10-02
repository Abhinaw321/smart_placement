package com.smartplacement.entity;

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
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an individual interview round scheduled between a candidate and an evaluation panel.
 */
@Entity
@Table(
        name = "interviews",
        indexes = {
                @Index(name = "idx_interviews_application_id", columnList = "application_id"),
                @Index(name = "idx_interviews_scheduled_at", columnList = "scheduled_at")
        }
)
public class Interview extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber = 1;

    @Column(name = "round_name", nullable = false, length = 100)
    private String roundName;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type", nullable = false, length = 30)
    private InterviewType interviewType = InterviewType.ONLINE_MEET;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "meeting_link_or_venue", nullable = false, length = 255)
    private String meetingLinkOrVenue;

    @Column(name = "interviewer_name", nullable = false, length = 100)
    private String interviewerName;

    @Column(name = "interviewer_email", length = 100)
    private String interviewerEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 30)
    private RoundResult result = RoundResult.PENDING;

    @Lob
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "conducted_at")
    private LocalDateTime conductedAt;

    public Interview() {
    }

    public Interview(Application application, Integer roundNumber, String roundName,
                     InterviewType interviewType, LocalDateTime scheduledAt,
                     String meetingLinkOrVenue, String interviewerName, String interviewerEmail) {
        this.application = application;
        this.roundNumber = roundNumber != null ? roundNumber : 1;
        this.roundName = roundName;
        this.interviewType = interviewType != null ? interviewType : InterviewType.ONLINE_MEET;
        this.scheduledAt = scheduledAt;
        this.meetingLinkOrVenue = meetingLinkOrVenue;
        this.interviewerName = interviewerName;
        this.interviewerEmail = interviewerEmail;
        this.status = InterviewStatus.SCHEDULED;
        this.result = RoundResult.PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interview interview = (Interview) o;
        return Objects.equals(id, interview.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
