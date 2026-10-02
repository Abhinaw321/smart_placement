package com.smartplacement.dto.job;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object encapsulating job qualification cutoffs and rules.
 */
public class EligibilityCriteriaDto {

    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "Minimum CGPA cannot exceed 10.0")
    private Double minCgpa = 0.0;

    @Min(value = 0, message = "Max active backlogs cannot be negative")
    private Integer maxActiveBacklogs = 0;

    @Min(value = 0, message = "Max history backlogs cannot be negative")
    private Integer maxHistoryBacklogs;

    @DecimalMin(value = "0.0", message = "10th percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "10th percentage cannot exceed 100.0")
    private Double minTenthPercentage = 0.0;

    @DecimalMin(value = "0.0", message = "12th percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "12th percentage cannot exceed 100.0")
    private Double minTwelfthPercentage = 0.0;

    @Min(value = 0, message = "Max gap years cannot be negative")
    private Integer maxGapYears;

    private List<String> allowedBranches = new ArrayList<>();
    private List<Integer> allowedGradYears = new ArrayList<>();
    private List<String> requiredSkills = new ArrayList<>();

    public EligibilityCriteriaDto() {
    }

    public EligibilityCriteriaDto(Double minCgpa, Integer maxActiveBacklogs, Integer maxHistoryBacklogs,
                                  Double minTenthPercentage, Double minTwelfthPercentage, Integer maxGapYears,
                                  List<String> allowedBranches, List<Integer> allowedGradYears, List<String> requiredSkills) {
        this.minCgpa = minCgpa;
        this.maxActiveBacklogs = maxActiveBacklogs;
        this.maxHistoryBacklogs = maxHistoryBacklogs;
        this.minTenthPercentage = minTenthPercentage;
        this.minTwelfthPercentage = minTwelfthPercentage;
        this.maxGapYears = maxGapYears;
        this.allowedBranches = allowedBranches != null ? allowedBranches : new ArrayList<>();
        this.allowedGradYears = allowedGradYears != null ? allowedGradYears : new ArrayList<>();
        this.requiredSkills = requiredSkills != null ? requiredSkills : new ArrayList<>();
    }

    public Double getMinCgpa() {
        return minCgpa;
    }

    public void setMinCgpa(Double minCgpa) {
        this.minCgpa = minCgpa;
    }

    public Integer getMaxActiveBacklogs() {
        return maxActiveBacklogs;
    }

    public void setMaxActiveBacklogs(Integer maxActiveBacklogs) {
        this.maxActiveBacklogs = maxActiveBacklogs;
    }

    public Integer getMaxHistoryBacklogs() {
        return maxHistoryBacklogs;
    }

    public void setMaxHistoryBacklogs(Integer maxHistoryBacklogs) {
        this.maxHistoryBacklogs = maxHistoryBacklogs;
    }

    public Double getMinTenthPercentage() {
        return minTenthPercentage;
    }

    public void setMinTenthPercentage(Double minTenthPercentage) {
        this.minTenthPercentage = minTenthPercentage;
    }

    public Double getMinTwelfthPercentage() {
        return minTwelfthPercentage;
    }

    public void setMinTwelfthPercentage(Double minTwelfthPercentage) {
        this.minTwelfthPercentage = minTwelfthPercentage;
    }

    public Integer getMaxGapYears() {
        return maxGapYears;
    }

    public void setMaxGapYears(Integer maxGapYears) {
        this.maxGapYears = maxGapYears;
    }

    public List<String> getAllowedBranches() {
        return allowedBranches;
    }

    public void setAllowedBranches(List<String> allowedBranches) {
        this.allowedBranches = allowedBranches;
    }

    public List<Integer> getAllowedGradYears() {
        return allowedGradYears;
    }

    public void setAllowedGradYears(List<Integer> allowedGradYears) {
        this.allowedGradYears = allowedGradYears;
    }

    public List<String> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
}
