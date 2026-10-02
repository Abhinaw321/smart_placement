package com.smartplacement.engine.eligibility;

import com.smartplacement.engine.eligibility.evaluator.EligibilityCriteriaEvaluator;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Strategy-pattern Composite Eligibility Engine.
 * Dynamically aggregates and executes all registered {@link EligibilityCriteriaEvaluator}
 * components against a candidate profile, compiling a comprehensive qualification report.
 */
@Service
public class EligibilityEngine {

    private static final Logger log = LoggerFactory.getLogger(EligibilityEngine.class);

    private final List<EligibilityCriteriaEvaluator> evaluators;

    public EligibilityEngine(List<EligibilityCriteriaEvaluator> evaluators) {
        this.evaluators = evaluators;
        log.info("Initialized EligibilityEngine with {} pluggable criteria evaluators.", evaluators.size());
    }

    /**
     * Executes all applicable criteria evaluators and compiles an exhaustive report.
     *
     * @param student Candidate profile to evaluate
     * @param criteria Job drive eligibility requirements
     * @return Aggregated {@link EligibilityReportDto} with granular pass/fail breakdown
     */
    public EligibilityReportDto evaluate(Student student, EligibilityCriteria criteria) {
        EligibilityReportDto report = new EligibilityReportDto();

        if (criteria == null) {
            log.debug("No eligibility criteria configured for job. Candidate is automatically eligible.");
            return report;
        }

        if (student == null) {
            report.setEligible(false);
            report.getRejectionReasons().add("Candidate profile is missing or uninitialized.");
            return report;
        }

        for (EligibilityCriteriaEvaluator evaluator : evaluators) {
            if (evaluator.isApplicable(criteria)) {
                CriterionEvaluationResult result = evaluator.evaluate(student, criteria);
                report.addCriterionResult(result);
            }
        }

        log.debug("Eligibility evaluation completed for student roll {}: eligible = {}, failures = {}",
                student.getRollNumber(), report.isEligible(), report.getRejectionReasons().size());

        return report;
    }

    /**
     * Fast-fail predicate check verifying if a candidate meets all criteria.
     */
    public boolean isEligible(Student student, EligibilityCriteria criteria) {
        return evaluate(student, criteria).isEligible();
    }
}
