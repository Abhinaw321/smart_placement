package com.smartplacement.engine.eligibility.evaluator;

import com.smartplacement.engine.eligibility.CriterionEvaluationResult;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Student;
import org.springframework.stereotype.Component;

/**
 * Evaluates candidate active and historical backlog counts against drive allowances.
 */
@Component
public class BacklogEvaluator implements EligibilityCriteriaEvaluator {

    @Override
    public String getCriterionName() {
        return "Backlog Count";
    }

    @Override
    public boolean isApplicable(EligibilityCriteria criteria) {
        return criteria != null && (criteria.getMaxActiveBacklogs() != null || criteria.getMaxHistoryBacklogs() != null);
    }

    @Override
    public CriterionEvaluationResult evaluate(Student student, EligibilityCriteria criteria) {
        int studentActive = (student != null && student.getActiveBacklogs() != null) ? student.getActiveBacklogs() : 0;
        int studentHistory = (student != null && student.getHistoryBacklogs() != null) ? student.getHistoryBacklogs() : 0;

        Integer maxActive = criteria.getMaxActiveBacklogs();
        Integer maxHistory = criteria.getMaxHistoryBacklogs();

        StringBuilder reqBuilder = new StringBuilder();
        StringBuilder actBuilder = new StringBuilder();

        if (maxActive != null) {
            reqBuilder.append("Active <= ").append(maxActive);
            actBuilder.append("Active: ").append(studentActive);
        }
        if (maxHistory != null) {
            if (reqBuilder.length() > 0) {
                reqBuilder.append(", ");
                actBuilder.append(", ");
            }
            reqBuilder.append("History <= ").append(maxHistory);
            actBuilder.append("History: ").append(studentHistory);
        }

        if (maxActive != null && studentActive > maxActive) {
            return CriterionEvaluationResult.failed(
                    getCriterionName(),
                    reqBuilder.toString(),
                    actBuilder.toString(),
                    String.format("Job allows a maximum of %d active backlog(s), but your record shows %d active backlog(s).", maxActive, studentActive)
            );
        }

        if (maxHistory != null && studentHistory > maxHistory) {
            return CriterionEvaluationResult.failed(
                    getCriterionName(),
                    reqBuilder.toString(),
                    actBuilder.toString(),
                    String.format("Job permits a maximum history of %d total backlog(s), but your record shows %d backlog attempt(s).", maxHistory, studentHistory)
            );
        }

        return CriterionEvaluationResult.passed(
                getCriterionName(),
                reqBuilder.toString(),
                actBuilder.toString(),
                "Backlog criteria satisfied."
        );
    }
}
