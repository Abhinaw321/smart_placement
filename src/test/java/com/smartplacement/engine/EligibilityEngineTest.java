package com.smartplacement.engine;

import com.smartplacement.engine.eligibility.CriterionEvaluationResult;
import com.smartplacement.engine.eligibility.EligibilityEngine;
import com.smartplacement.engine.eligibility.EligibilityReportDto;
import com.smartplacement.engine.eligibility.evaluator.BacklogEvaluator;
import com.smartplacement.engine.eligibility.evaluator.BranchEvaluator;
import com.smartplacement.engine.eligibility.evaluator.CgpaEvaluator;
import com.smartplacement.engine.eligibility.evaluator.GapYearEvaluator;
import com.smartplacement.engine.eligibility.evaluator.GraduationYearEvaluator;
import com.smartplacement.engine.eligibility.evaluator.SecondaryEducationEvaluator;
import com.smartplacement.engine.eligibility.evaluator.SkillSetEvaluator;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.SkillProficiency;
import com.smartplacement.entity.Student;
import com.smartplacement.entity.StudentSkill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EligibilityEngineTest {

    private EligibilityEngine engine;
    private CgpaEvaluator cgpaEvaluator;
    private BacklogEvaluator backlogEvaluator;
    private BranchEvaluator branchEvaluator;
    private GraduationYearEvaluator gradYearEvaluator;
    private SecondaryEducationEvaluator secEduEvaluator;
    private SkillSetEvaluator skillEvaluator;
    private GapYearEvaluator gapYearEvaluator;

    @BeforeEach
    void setUp() {
        cgpaEvaluator = new CgpaEvaluator();
        backlogEvaluator = new BacklogEvaluator();
        branchEvaluator = new BranchEvaluator();
        gradYearEvaluator = new GraduationYearEvaluator();
        secEduEvaluator = new SecondaryEducationEvaluator();
        skillEvaluator = new SkillSetEvaluator();
        gapYearEvaluator = new GapYearEvaluator();

        engine = new EligibilityEngine(Arrays.asList(
                cgpaEvaluator,
                backlogEvaluator,
                branchEvaluator,
                gradYearEvaluator,
                secEduEvaluator,
                skillEvaluator,
                gapYearEvaluator
        ));
    }

    private Student createStandardStudent() {
        Student student = new Student();
        student.setRollNumber("2024CS001");
        student.setFirstName("Rohan");
        student.setLastName("Sharma");
        student.setBranch("Computer Science and Engineering");
        student.setGraduationYear(2026);
        student.setCgpa(8.25);
        student.setActiveBacklogs(0);
        student.setHistoryBacklogs(0);
        student.setTenthPercentage(91.5);
        student.setTwelfthPercentage(87.0);
        student.setGapYears(0);

        student.addSkill(new StudentSkill("Java", SkillProficiency.ADVANCED));
        student.addSkill(new StudentSkill("Spring Boot", SkillProficiency.INTERMEDIATE));
        student.addSkill(new StudentSkill("SQL", SkillProficiency.INTERMEDIATE));

        return student;
    }

    private EligibilityCriteria createStandardCriteria() {
        EligibilityCriteria criteria = new EligibilityCriteria();
        criteria.setMinCgpa(7.5);
        criteria.setMaxActiveBacklogs(0);
        criteria.setMaxHistoryBacklogs(1);
        criteria.setMinTenthPercentage(70.0);
        criteria.setMinTwelfthPercentage(70.0);
        criteria.setMaxGapYears(1);
        criteria.setAllowedBranches(Arrays.asList("Computer Science and Engineering", "Information Technology"));
        criteria.setAllowedGradYears(Arrays.asList(2026));
        criteria.setRequiredSkills(Arrays.asList("Java", "SQL"));
        return criteria;
    }

    @Test
    @DisplayName("Should pass fully when candidate satisfies all configured criteria")
    void shouldPassWhenCandidateMeetsAllCriteria() {
        Student student = createStandardStudent();
        EligibilityCriteria criteria = createStandardCriteria();

        EligibilityReportDto report = engine.evaluate(student, criteria);

        assertTrue(report.isEligible());
        assertTrue(report.getRejectionReasons().isEmpty());
        assertEquals(7, report.getCriterionResults().size());
    }

    @Test
    @DisplayName("CGPA Evaluator: Should fail when student CGPA is below cutoff")
    void shouldFailWhenCgpaIsBelowThreshold() {
        Student student = createStandardStudent();
        student.setCgpa(7.10); // Below 7.50

        EligibilityCriteria criteria = createStandardCriteria();

        EligibilityReportDto report = engine.evaluate(student, criteria);

        assertFalse(report.isEligible());
        assertEquals(1, report.getRejectionReasons().size());
        assertTrue(report.getRejectionReasons().get(0).contains("Minimum CGPA required is 7.50, but your current CGPA is 7.10"));
    }

    @Test
    @DisplayName("Backlog Evaluator: Should fail when student has active backlog exceeding drive allowance")
    void shouldFailWhenActiveBacklogsExceedLimit() {
        Student student = createStandardStudent();
        student.setActiveBacklogs(1); // Limit is 0

        EligibilityCriteria criteria = createStandardCriteria();

        EligibilityReportDto report = engine.evaluate(student, criteria);

        assertFalse(report.isEligible());
        assertTrue(report.getRejectionReasons().stream()
                .anyMatch(r -> r.contains("Job allows a maximum of 0 active backlog(s), but your record shows 1 active backlog(s)")));
    }

    @Test
    @DisplayName("Branch Evaluator: Should fail when student branch is not in allowed list")
    void shouldFailWhenBranchIsNotAllowed() {
        Student student = createStandardStudent();
        student.setBranch("Mechanical Engineering");

        EligibilityCriteria criteria = createStandardCriteria();

        EligibilityReportDto report = engine.evaluate(student, criteria);

        assertFalse(report.isEligible());
        assertTrue(report.getRejectionReasons().stream()
                .anyMatch(r -> r.contains("Branch 'Mechanical Engineering' is not eligible")));
    }

    @Test
    @DisplayName("Graduation Year Evaluator: Should fail when batch does not match")
    void shouldFailWhenGraduationYearDoesNotMatch() {
        Student student = createStandardStudent();
        student.setGraduationYear(2027); // Allowed is 2026

        EligibilityCriteria criteria = createStandardCriteria();

        EligibilityReportDto report = engine.evaluate(student, criteria);

        assertFalse(report.isEligible());
        assertTrue(report.getRejectionReasons().stream()
                .anyMatch(r -> r.contains("Graduation year 2027 is not eligible. Eligible batch(es): 2026")));
    }

    @Test
    @DisplayName("Secondary Education Evaluator: Should pass lateral diploma students when 12th is missing but diploma meets cutoff")
    void shouldPassLateralDiplomaStudentWhenDiplomaExceedsCutoff() {
        Student student = createStandardStudent();
        student.setTwelfthPercentage(null);
        student.setDiplomaPercentage(78.5); // Required 12th/Diploma is 70.0

        EligibilityCriteria criteria = createStandardCriteria();

        EligibilityReportDto report = engine.evaluate(student, criteria);

        assertTrue(report.isEligible());
    }

    @Test
    @DisplayName("Secondary Education Evaluator: Should fail when 10th percentage is below minimum")
    void shouldFailWhenTenthPercentageBelowMinimum() {
        Student student = createStandardStudent();
        student.setTenthPercentage(64.0); // Required is 70.0

        EligibilityCriteria criteria = createStandardCriteria();

        EligibilityReportDto report = engine.evaluate(student, criteria);

        assertFalse(report.isEligible());
        assertTrue(report.getRejectionReasons().stream()
                .anyMatch(r -> r.contains("10th percentage requirement is 70.00%, but your record shows 64.00%")));
    }

    @Test
    @DisplayName("Skill Set Evaluator: Should fail and list specific missing mandatory skills")
    void shouldFailWhenMandatorySkillsAreMissing() {
        Student student = createStandardStudent();
        student.getSkills().clear();
        student.addSkill(new StudentSkill("Python", SkillProficiency.INTERMEDIATE));

        EligibilityCriteria criteria = createStandardCriteria(); // Requires Java, SQL

        EligibilityReportDto report = engine.evaluate(student, criteria);

        assertFalse(report.isEligible());
        assertTrue(report.getRejectionReasons().stream()
                .anyMatch(r -> r.contains("Missing mandatory required skill(s): Java, SQL")));
    }

    @Test
    @DisplayName("Gap Year Evaluator: Should fail when candidate gap years exceed policy")
    void shouldFailWhenGapYearsExceedPolicy() {
        Student student = createStandardStudent();
        student.setGapYears(2); // Maximum permitted is 1

        EligibilityCriteria criteria = createStandardCriteria();

        EligibilityReportDto report = engine.evaluate(student, criteria);

        assertFalse(report.isEligible());
        assertTrue(report.getRejectionReasons().stream()
                .anyMatch(r -> r.contains("Maximum permitted study gap is 1 year(s), but your record shows 2 year(s)")));
    }

    @Test
    @DisplayName("Composite Evaluation: Should collect multiple simultaneous rejection reasons")
    void shouldCollectMultipleSimultaneousRejectionReasons() {
        Student student = createStandardStudent();
        student.setCgpa(6.40); // Fail 1 (CGPA)
        student.setActiveBacklogs(2); // Fail 2 (Backlogs)
        student.setBranch("Civil Engineering"); // Fail 3 (Branch)

        EligibilityCriteria criteria = createStandardCriteria();

        EligibilityReportDto report = engine.evaluate(student, criteria);

        assertFalse(report.isEligible());
        assertEquals(3, report.getRejectionReasons().size());
    }
}
