package com.smartplacement.dto.student;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StudentAcademicUpdateDto {

    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    @NotBlank(message = "Branch is required")
    private String branch;

    @NotNull(message = "Graduation year is required")
    private Integer graduationYear;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10.0")
    private Double cgpa;

    @NotNull(message = "Active backlogs count is required")
    @Min(value = 0, message = "Active backlogs cannot be negative")
    private Integer activeBacklogs = 0;

    @NotNull(message = "History backlogs count is required")
    @Min(value = 0, message = "History backlogs cannot be negative")
    private Integer historyBacklogs = 0;

    @NotNull(message = "10th percentage is required")
    @DecimalMin(value = "0.0", message = "10th percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "10th percentage cannot exceed 100.0")
    private Double tenthPercentage = 0.0;

    @DecimalMin(value = "0.0", message = "12th percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "12th percentage cannot exceed 100.0")
    private Double twelfthPercentage;

    @DecimalMin(value = "0.0", message = "Diploma percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Diploma percentage cannot exceed 100.0")
    private Double diplomaPercentage;

    @NotNull(message = "Gap years count is required")
    @Min(value = 0, message = "Gap years cannot be negative")
    private Integer gapYears = 0;

    public StudentAcademicUpdateDto() {
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getActiveBacklogs() {
        return activeBacklogs;
    }

    public void setActiveBacklogs(Integer activeBacklogs) {
        this.activeBacklogs = activeBacklogs;
    }

    public Integer getHistoryBacklogs() {
        return historyBacklogs;
    }

    public void setHistoryBacklogs(Integer historyBacklogs) {
        this.historyBacklogs = historyBacklogs;
    }

    public Double getTenthPercentage() {
        return tenthPercentage;
    }

    public void setTenthPercentage(Double tenthPercentage) {
        this.tenthPercentage = tenthPercentage;
    }

    public Double getTwelfthPercentage() {
        return twelfthPercentage;
    }

    public void setTwelfthPercentage(Double twelfthPercentage) {
        this.twelfthPercentage = twelfthPercentage;
    }

    public Double getDiplomaPercentage() {
        return diplomaPercentage;
    }

    public void setDiplomaPercentage(Double diplomaPercentage) {
        this.diplomaPercentage = diplomaPercentage;
    }

    public Integer getGapYears() {
        return gapYears;
    }

    public void setGapYears(Integer gapYears) {
        this.gapYears = gapYears;
    }
}
