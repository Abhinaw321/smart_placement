package com.smartplacement.engine.eligibility;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite report representing the complete eligibility evaluation for a candidate against a job.
 */
public class EligibilityReportDto {

    private boolean eligible = true;
    private List<CriterionEvaluationResult> criterionResults = new ArrayList<>();
    private List<String> rejectionReasons = new ArrayList<>();

    public EligibilityReportDto() {
    }

    public void addCriterionResult(CriterionEvaluationResult result) {
        criterionResults.add(result);
        if (!result.isEligible()) {
            this.eligible = false;
            this.rejectionReasons.add(result.getMessage());
        }
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public List<CriterionEvaluationResult> getCriterionResults() {
        return criterionResults;
    }

    public void setCriterionResults(List<CriterionEvaluationResult> criterionResults) {
        this.criterionResults = criterionResults;
    }

    public List<String> getRejectionReasons() {
        return rejectionReasons;
    }

    public void setRejectionReasons(List<String> rejectionReasons) {
        this.rejectionReasons = rejectionReasons;
    }
}
