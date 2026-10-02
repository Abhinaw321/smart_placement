package com.smartplacement.engine.eligibility.evaluator;

import com.smartplacement.engine.eligibility.CriterionEvaluationResult;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Student;
import com.smartplacement.entity.StudentSkill;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Evaluates whether a candidate profile lists all mandatory technical skills specified for the role.
 */
@Component
public class SkillSetEvaluator implements EligibilityCriteriaEvaluator {

    @Override
    public String getCriterionName() {
        return "Mandatory Technical Skills";
    }

    @Override
    public boolean isApplicable(EligibilityCriteria criteria) {
        return criteria != null && criteria.getRequiredSkills() != null && !criteria.getRequiredSkills().isEmpty();
    }

    @Override
    public CriterionEvaluationResult evaluate(Student student, EligibilityCriteria criteria) {
        List<String> requiredSkills = criteria.getRequiredSkills();

        Set<String> studentSkillNames = (student != null && student.getSkills() != null)
                ? student.getSkills().stream()
                .filter(s -> s.getSkillName() != null)
                .map(s -> s.getSkillName().trim().toLowerCase())
                .collect(Collectors.toSet())
                : Collections.emptySet();

        List<String> missingSkills = new ArrayList<>();
        for (String req : requiredSkills) {
            if (req != null && !req.trim().isEmpty() && !studentSkillNames.contains(req.trim().toLowerCase())) {
                missingSkills.add(req.trim());
            }
        }

        String reqStr = String.join(", ", requiredSkills);
        String actStr = (student != null && student.getSkills() != null && !student.getSkills().isEmpty())
                ? student.getSkills().stream().map(StudentSkill::getSkillName).collect(Collectors.joining(", "))
                : "No skills listed";

        if (missingSkills.isEmpty()) {
            return CriterionEvaluationResult.passed(
                    getCriterionName(),
                    reqStr,
                    actStr,
                    "All mandatory skill requirements satisfied."
            );
        } else {
            return CriterionEvaluationResult.failed(
                    getCriterionName(),
                    reqStr,
                    actStr,
                    String.format("Missing mandatory required skill(s): %s.", String.join(", ", missingSkills))
            );
        }
    }
}
