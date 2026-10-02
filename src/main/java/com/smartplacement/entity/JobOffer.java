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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an official employment or internship offer issued to a candidate.
 */
@Entity
@Table(
        name = "job_offers",
        indexes = {
                @Index(name = "idx_offers_application_id", columnList = "application_id", unique = true),
                @Index(name = "idx_offers_student_id", columnList = "student_id"),
                @Index(name = "idx_offers_job_id", columnList = "job_id"),
                @Index(name = "idx_offers_status", columnList = "status")
        }
)
public class JobOffer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(name = "ctc_lpa", nullable = false)
    private Double ctcLpa;

    @Column(name = "designation", nullable = false, length = 120)
    private String designation;

    @Column(name = "offer_letter_url", length = 255)
    private String offerLetterUrl;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "valid_until", nullable = false)
    private LocalDate validUntil;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OfferStatus status = OfferStatus.PENDING;

    @Column(name = "response_date")
    private LocalDateTime responseDate;

    @Lob
    @Column(name = "student_remarks", columnDefinition = "TEXT")
    private String studentRemarks;

    @Lob
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public JobOffer() {
    }

    public JobOffer(Application application, Student student, Job job, Double ctcLpa,
                    String designation, LocalDate issueDate, LocalDate validUntil,
                    LocalDate joiningDate, String offerLetterUrl, String notes) {
        this.application = application;
        this.student = student;
        this.job = job;
        this.ctcLpa = ctcLpa;
        this.designation = designation;
        this.issueDate = issueDate != null ? issueDate : LocalDate.now();
        this.validUntil = validUntil;
        this.joiningDate = joiningDate;
        this.offerLetterUrl = offerLetterUrl;
        this.notes = notes;
        this.status = OfferStatus.PENDING;
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobOffer jobOffer = (JobOffer) o;
        return Objects.equals(id, jobOffer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
