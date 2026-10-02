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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an individual student's formal job application and workflow state.
 */
@Entity
@Table(
        name = "applications",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_applications_job_student", columnNames = {"job_id", "student_id"})
        },
        indexes = {
                @Index(name = "idx_applications_job_status", columnList = "job_id, status"),
                @Index(name = "idx_applications_student_id", columnList = "student_id")
        }
)
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "current_round", length = 100)
    private String currentRound = "Initial Application";

    @Column(name = "resume_snapshot_url", length = 255)
    private String resumeSnapshotUrl;

    @Lob
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    public Application() {
    }

    public Application(Job job, Student student, String resumeSnapshotUrl) {
        this.job = job;
        this.student = student;
        this.resumeSnapshotUrl = resumeSnapshotUrl;
        this.status = ApplicationStatus.APPLIED;
        this.currentRound = "Application Submitted";
        this.appliedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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

    public String getResumeSnapshotUrl() {
        return resumeSnapshotUrl;
    }

    public void setResumeSnapshotUrl(String resumeSnapshotUrl) {
        this.resumeSnapshotUrl = resumeSnapshotUrl;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Application that = (Application) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
