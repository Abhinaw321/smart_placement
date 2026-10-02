package com.smartplacement.engine.eligibility.evaluator;

import com.smartplacement.engine.eligibility.CriterionEvaluationResult;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Student;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Evaluates student academic department/branch against allowed branches for the drive.
 */
@Component
public class BranchEvaluator implements EligibilityCriteriaEvaluator {

    @Override
    public String getCriterionName() {
        return "Allowed Branches";
    }

    @Override
    public boolean isApplicable(EligibilityCriteria criteria) {
        return criteria != null && criteria.getAllowedBranches() != null && !criteria.getAllowedBranches().isEmpty();
    }

    @Override
    public CriterionEvaluationResult evaluate(Student student, EligibilityCriteria criteria) {
        List<String> allowedBranches = criteria.getAllowedBranches();
        String studentBranch = (student != null && student.getBranch() != null) ? student.getBranch().trim() : "";

        String reqStr = String.join(", ", allowedBranches);
        String actStr = studentBranch.isEmpty() ? "None specified" : studentBranch;

        boolean matches = allowedBranches.stream()
                .anyMatch(b -> b.trim().equalsIgnoreCase(studentBranch));

        if (matches) {
            return CriterionEvaluationResult.passed(
                    getCriterionName(),
                    reqStr,
                    actStr,
                    String.format("Branch '%s' is eligible for this opportunity.", studentBranch)
            );
        } else {
            return CriterionEvaluationResult.failed(
                    getCriterionName(),
                    reqStr,
                    actStr,
                    String.format("Branch '%s' is not eligible. Eligible branch(es): %s.", actStr, reqStr)
            );
        }
    }
}
