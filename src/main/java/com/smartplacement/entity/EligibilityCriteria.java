package com.smartplacement.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity storing quantitative and categorical criteria required for a student
 * to qualify and apply for a specific job drive.
 */
@Entity
@Table(name = "eligibility_criteria")
public class EligibilityCriteria extends BaseEntity {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "job_id", nullable = false, unique = true)
    private Job job;

    @Column(name = "min_cgpa")
    private Double minCgpa = 0.0;

    @Column(name = "max_active_backlogs")
    private Integer maxActiveBacklogs = 0;

    @Column(name = "max_history_backlogs")
    private Integer maxHistoryBacklogs;

    @Column(name = "min_tenth_percentage")
    private Double minTenthPercentage = 0.0;

    @Column(name = "min_twelfth_percentage")
    private Double minTwelfthPercentage = 0.0;

    @Column(name = "max_gap_years")
    private Integer maxGapYears;

    @Lob
    @Column(name = "allowed_branches_json", columnDefinition = "TEXT")
    private String allowedBranchesJson;

    @Lob
    @Column(name = "allowed_grad_years_json", columnDefinition = "TEXT")
    private String allowedGradYearsJson;

    @Lob
    @Column(name = "required_skills_json", columnDefinition = "TEXT")
    private String requiredSkillsJson;

    public EligibilityCriteria() {
    }

    public EligibilityCriteria(Job job, Double minCgpa, Integer maxActiveBacklogs, Integer maxHistoryBacklogs,
                               Double minTenthPercentage, Double minTwelfthPercentage, Integer maxGapYears) {
        this.job = job;
        this.minCgpa = minCgpa != null ? minCgpa : 0.0;
        this.maxActiveBacklogs = maxActiveBacklogs != null ? maxActiveBacklogs : 0;
        this.maxHistoryBacklogs = maxHistoryBacklogs;
        this.minTenthPercentage = minTenthPercentage != null ? minTenthPercentage : 0.0;
        this.minTwelfthPercentage = minTwelfthPercentage != null ? minTwelfthPercentage : 0.0;
        this.maxGapYears = maxGapYears;
    }

    // Helper methods for JSON parsing
    public List<String> getAllowedBranches() {
        if (allowedBranchesJson == null || allowedBranchesJson.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(allowedBranchesJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void setAllowedBranches(List<String> branches) {
        if (branches == null || branches.isEmpty()) {
            this.allowedBranchesJson = "[]";
            return;
        }
        try {
            this.allowedBranchesJson = OBJECT_MAPPER.writeValueAsString(branches);
        } catch (Exception e) {
            this.allowedBranchesJson = "[]";
        }
    }

    public List<Integer> getAllowedGradYears() {
        if (allowedGradYearsJson == null || allowedGradYearsJson.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(allowedGradYearsJson, new TypeReference<List<Integer>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void setAllowedGradYears(List<Integer> years) {
        if (years == null || years.isEmpty()) {
            this.allowedGradYearsJson = "[]";
            return;
        }
        try {
            this.allowedGradYearsJson = OBJECT_MAPPER.writeValueAsString(years);
        } catch (Exception e) {
            this.allowedGradYearsJson = "[]";
        }
    }

    public List<String> getRequiredSkills() {
        if (requiredSkillsJson == null || requiredSkillsJson.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(requiredSkillsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void setRequiredSkills(List<String> skills) {
        if (skills == null || skills.isEmpty()) {
            this.requiredSkillsJson = "[]";
            return;
        }
        try {
            this.requiredSkillsJson = OBJECT_MAPPER.writeValueAsString(skills);
        } catch (Exception e) {
            this.requiredSkillsJson = "[]";
        }
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

    public String getAllowedBranchesJson() {
        return allowedBranchesJson;
    }

    public void setAllowedBranchesJson(String allowedBranchesJson) {
        this.allowedBranchesJson = allowedBranchesJson;
    }

    public String getAllowedGradYearsJson() {
        return allowedGradYearsJson;
    }

    public void setAllowedGradYearsJson(String allowedGradYearsJson) {
        this.allowedGradYearsJson = allowedGradYearsJson;
    }

    public String getRequiredSkillsJson() {
        return requiredSkillsJson;
    }

    public void setRequiredSkillsJson(String requiredSkillsJson) {
        this.requiredSkillsJson = requiredSkillsJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EligibilityCriteria that = (EligibilityCriteria) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
