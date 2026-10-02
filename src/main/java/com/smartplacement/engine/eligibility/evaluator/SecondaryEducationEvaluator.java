package com.smartplacement.engine.eligibility.evaluator;

import com.smartplacement.engine.eligibility.CriterionEvaluationResult;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Student;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Evaluates student secondary (10th) and higher secondary / diploma (12th) percentages.
 */
@Component
public class SecondaryEducationEvaluator implements EligibilityCriteriaEvaluator {

    @Override
    public String getCriterionName() {
        return "Secondary Education (10th/12th/Diploma)";
    }

    @Override
    public boolean isApplicable(EligibilityCriteria criteria) {
        if (criteria == null) return false;
        boolean hasTenth = criteria.getMinTenthPercentage() != null && criteria.getMinTenthPercentage() > 0.0;
        boolean hasTwelfth = criteria.getMinTwelfthPercentage() != null && criteria.getMinTwelfthPercentage() > 0.0;
        return hasTenth || hasTwelfth;
    }

    @Override
    public CriterionEvaluationResult evaluate(Student student, EligibilityCriteria criteria) {
        Double minTenth = criteria.getMinTenthPercentage();
        Double minTwelfth = criteria.getMinTwelfthPercentage();

        double studentTenth = (student != null && student.getTenthPercentage() != null) ? student.getTenthPercentage() : 0.0;
        double studentTwelfth = (student != null && student.getTwelfthPercentage() != null) ? student.getTwelfthPercentage() : 0.0;
        double studentDiploma = (student != null && student.getDiplomaPercentage() != null) ? student.getDiplomaPercentage() : 0.0;
        double studentHigherSec = Math.max(studentTwelfth, studentDiploma);

        StringBuilder reqBuilder = new StringBuilder();
        StringBuilder actBuilder = new StringBuilder();

        if (minTenth != null && minTenth > 0.0) {
            reqBuilder.append(String.format(Locale.ROOT, "10th >= %.2f%%", minTenth));
            actBuilder.append(String.format(Locale.ROOT, "10th: %.2f%%", studentTenth));
        }
        if (minTwelfth != null && minTwelfth > 0.0) {
            if (reqBuilder.length() > 0) {
                reqBuilder.append(", ");
                actBuilder.append(", ");
            }
            reqBuilder.append(String.format(Locale.ROOT, "12th/Diploma >= %.2f%%", minTwelfth));
            actBuilder.append(String.format(Locale.ROOT, "12th/Dip: %.2f%%", studentHigherSec));
        }

        if (minTenth != null && minTenth > 0.0 && studentTenth < minTenth) {
            return CriterionEvaluationResult.failed(
                    getCriterionName(),
                    reqBuilder.toString(),
                    actBuilder.toString(),
                    String.format(Locale.ROOT, "10th percentage requirement is %.2f%%, but your record shows %.2f%%.", minTenth, studentTenth)
            );
        }

        if (minTwelfth != null && minTwelfth > 0.0 && studentHigherSec < minTwelfth) {
            return CriterionEvaluationResult.failed(
                    getCriterionName(),
                    reqBuilder.toString(),
                    actBuilder.toString(),
                    String.format(Locale.ROOT, "12th / Diploma percentage requirement is %.2f%%, but your highest record is %.2f%%.", minTwelfth, studentHigherSec)
            );
        }

        return CriterionEvaluationResult.passed(
                getCriterionName(),
                reqBuilder.toString(),
                actBuilder.toString(),
                "Secondary education percentages meet or exceed requirements."
        );
    }
}
