package com.smartplacement.engine.eligibility.evaluator;

import com.smartplacement.engine.eligibility.CriterionEvaluationResult;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Student;

/**
 * Strategy interface representing an isolated, pluggable eligibility rule evaluator.
 * Each implementation validates one specific qualification domain (e.g. CGPA, backlogs, branch).
 */
public interface EligibilityCriteriaEvaluator {

    /**
     * Unique display name of this criterion rule.
     */
    String getCriterionName();

    /**
     * Checks if this rule is configured/active on the specified job criteria.
     */
    boolean isApplicable(EligibilityCriteria criteria);

    /**
     * Evaluates whether the given student satisfies this criterion.
     */
    CriterionEvaluationResult evaluate(Student student, EligibilityCriteria criteria);
}
