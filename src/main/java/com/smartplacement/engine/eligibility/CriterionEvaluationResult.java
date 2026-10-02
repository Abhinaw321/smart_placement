package com.smartplacement.engine.eligibility;

/**
 * Result model representing the outcome of checking a specific eligibility criterion.
 */
public class CriterionEvaluationResult {

    private String criterionName;
    private boolean eligible;
    private String requiredValue;
    private String actualValue;
    private String message;

    public CriterionEvaluationResult() {
    }

    public CriterionEvaluationResult(String criterionName, boolean eligible, String requiredValue, String actualValue, String message) {
        this.criterionName = criterionName;
        this.eligible = eligible;
        this.requiredValue = requiredValue;
        this.actualValue = actualValue;
        this.message = message;
    }

    public static CriterionEvaluationResult passed(String criterionName, String requiredValue, String actualValue, String message) {
        return new CriterionEvaluationResult(criterionName, true, requiredValue, actualValue, message);
    }

    public static CriterionEvaluationResult failed(String criterionName, String requiredValue, String actualValue, String message) {
        return new CriterionEvaluationResult(criterionName, false, requiredValue, actualValue, message);
    }

    public String getCriterionName() {
        return criterionName;
    }

    public void setCriterionName(String criterionName) {
        this.criterionName = criterionName;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public String getRequiredValue() {
        return requiredValue;
    }

    public void setRequiredValue(String requiredValue) {
        this.requiredValue = requiredValue;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
