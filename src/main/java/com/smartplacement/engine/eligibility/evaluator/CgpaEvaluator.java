package com.smartplacement.engine.eligibility.evaluator;

import com.smartplacement.engine.eligibility.CriterionEvaluationResult;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Student;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Evaluates candidate cumulative grade point average (CGPA) against drive minimum.
 */
@Component
public class CgpaEvaluator implements EligibilityCriteriaEvaluator {

    @Override
    public String getCriterionName() {
        return "Minimum CGPA";
    }

    @Override
    public boolean isApplicable(EligibilityCriteria criteria) {
        return criteria != null && criteria.getMinCgpa() != null && criteria.getMinCgpa() > 0.0;
    }

    @Override
    public CriterionEvaluationResult evaluate(Student student, EligibilityCriteria criteria) {
        double minRequired = criteria.getMinCgpa();
        double actualCgpa = (student != null && student.getCgpa() != null) ? student.getCgpa() : 0.0;

        String reqStr = String.format(Locale.ROOT, ">= %.2f", minRequired);
        String actStr = String.format(Locale.ROOT, "%.2f", actualCgpa);

        if (actualCgpa >= minRequired) {
            return CriterionEvaluationResult.passed(
                    getCriterionName(),
                    reqStr,
                    actStr,
                    String.format(Locale.ROOT, "CGPA requirement satisfied (%.2f >= %.2f).", actualCgpa, minRequired)
            );
        } else {
            return CriterionEvaluationResult.failed(
                    getCriterionName(),
                    reqStr,
                    actStr,
                    String.format(Locale.ROOT, "Minimum CGPA required is %.2f, but your current CGPA is %.2f.", minRequired, actualCgpa)
            );
        }
    }
}
