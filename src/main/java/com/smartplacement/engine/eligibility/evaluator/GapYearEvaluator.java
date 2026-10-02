package com.smartplacement.engine.eligibility.evaluator;

import com.smartplacement.engine.eligibility.CriterionEvaluationResult;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Student;
import org.springframework.stereotype.Component;

/**
 * Evaluates candidate career or education gap years against company policies.
 */
@Component
public class GapYearEvaluator implements EligibilityCriteriaEvaluator {

    @Override
    public String getCriterionName() {
        return "Study Gap Years";
    }

    @Override
    public boolean isApplicable(EligibilityCriteria criteria) {
        return criteria != null && criteria.getMaxGapYears() != null;
    }

    @Override
    public CriterionEvaluationResult evaluate(Student student, EligibilityCriteria criteria) {
        int maxGap = criteria.getMaxGapYears();
        int studentGap = (student != null && student.getGapYears() != null) ? student.getGapYears() : 0;

        String reqStr = String.format("<= %d year(s)", maxGap);
        String actStr = String.format("%d year(s)", studentGap);

        if (studentGap <= maxGap) {
            return CriterionEvaluationResult.passed(
                    getCriterionName(),
                    reqStr,
                    actStr,
                    "Study gap criteria satisfied."
            );
        } else {
            return CriterionEvaluationResult.failed(
                    getCriterionName(),
                    reqStr,
                    actStr,
                    String.format("Maximum permitted study gap is %d year(s), but your record shows %d year(s).", maxGap, studentGap)
            );
        }
    }
}
