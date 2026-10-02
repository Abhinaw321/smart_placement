package com.smartplacement.engine.eligibility.evaluator;

import com.smartplacement.engine.eligibility.CriterionEvaluationResult;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Student;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Evaluates candidate expected graduation year against designated placement drive batches.
 */
@Component
public class GraduationYearEvaluator implements EligibilityCriteriaEvaluator {

    @Override
    public String getCriterionName() {
        return "Graduation Year";
    }

    @Override
    public boolean isApplicable(EligibilityCriteria criteria) {
        return criteria != null && criteria.getAllowedGradYears() != null && !criteria.getAllowedGradYears().isEmpty();
    }

    @Override
    public CriterionEvaluationResult evaluate(Student student, EligibilityCriteria criteria) {
        List<Integer> allowedYears = criteria.getAllowedGradYears();
        Integer studentYear = (student != null) ? student.getGraduationYear() : null;

        String reqStr = allowedYears.stream().map(String::valueOf).collect(Collectors.joining(", "));
        String actStr = (studentYear != null) ? String.valueOf(studentYear) : "Not recorded";

        boolean matches = studentYear != null && allowedYears.contains(studentYear);

        if (matches) {
            return CriterionEvaluationResult.passed(
                    getCriterionName(),
                    reqStr,
                    actStr,
                    String.format("Graduation year %d is eligible.", studentYear)
            );
        } else {
            return CriterionEvaluationResult.failed(
                    getCriterionName(),
                    reqStr,
                    actStr,
                    String.format("Graduation year %s is not eligible. Eligible batch(es): %s.", actStr, reqStr)
            );
        }
    }
}
