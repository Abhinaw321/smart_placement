package com.smartplacement.dto.job;

import com.smartplacement.engine.eligibility.CriterionEvaluationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Detailed real-time candidate eligibility feedback for a specific job drive.
 */
public class StudentEligibilityCheckResponseDto {

    private Long jobId;
    private String jobTitle;
    private String companyName;
    private Long studentId;
    private String studentRollNumber;
    private String studentName;
    private boolean eligible;
    private List<String> rejectionReasons = new ArrayList<>();
    private List<CriterionEvaluationResult> criterionResults = new ArrayList<>();

    public StudentEligibilityCheckResponseDto() {
    }

    public StudentEligibilityCheckResponseDto(Long jobId, String jobTitle, String companyName,
                                             Long studentId, String studentRollNumber, String studentName,
                                             boolean eligible, List<String> rejectionReasons,
                                             List<CriterionEvaluationResult> criterionResults) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.studentId = studentId;
        this.studentRollNumber = studentRollNumber;
        this.studentName = studentName;
        this.eligible = eligible;
        this.rejectionReasons = rejectionReasons != null ? rejectionReasons : new ArrayList<>();
        this.criterionResults = criterionResults != null ? criterionResults : new ArrayList<>();
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public List<String> getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(List<String> rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }

    public List<CriterionEvaluationResult> getCriterionResults() {
        return criterionResults;
    }

    public void setCriterionResults(List<CriterionEvaluationResult> criterionResults) {
        this.criterionResults = criterionResults;
    }
}
