package com.smartplacement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartplacement.dto.application.ApplicationStatusUpdateDto;
import com.smartplacement.dto.application.BatchApplicationStatusUpdateDto;
import com.smartplacement.dto.auth.AuthResponseDto;
import com.smartplacement.dto.auth.LoginRequestDto;
import com.smartplacement.dto.auth.RecruiterRegisterRequestDto;
import com.smartplacement.dto.auth.StudentRegisterRequestDto;
import com.smartplacement.dto.job.EligibilityCriteriaDto;
import com.smartplacement.dto.job.JobCreateRequestDto;
import com.smartplacement.entity.Application;
import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.entity.Company;
import com.smartplacement.entity.Job;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.SkillProficiency;
import com.smartplacement.entity.Student;
import com.smartplacement.entity.StudentSkill;
import com.smartplacement.entity.User;
import com.smartplacement.entity.UserStatus;
import com.smartplacement.repository.ApplicationRepository;
import com.smartplacement.repository.CompanyRepository;
import com.smartplacement.repository.EligibilityCriteriaRepository;
import com.smartplacement.repository.JobRepository;
import com.smartplacement.repository.RecruiterRepository;
import com.smartplacement.repository.StudentCertificationRepository;
import com.smartplacement.repository.StudentProjectRepository;
import com.smartplacement.repository.StudentRepository;
import com.smartplacement.repository.StudentSkillRepository;
import com.smartplacement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EligibilityCriteriaRepository eligibilityCriteriaRepository;

    @Autowired
    private StudentCertificationRepository certificationRepository;

    @Autowired
    private StudentProjectRepository projectRepository;

    @Autowired
    private StudentSkillRepository skillRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        applicationRepository.deleteAll();
        jobRepository.deleteAll();
        eligibilityCriteriaRepository.deleteAll();
        certificationRepository.deleteAll();
        projectRepository.deleteAll();
        skillRepository.deleteAll();
        studentRepository.deleteAll();
        recruiterRepository.deleteAll();
        companyRepository.deleteAll();
        userRepository.deleteAll();

        // Reseed TPO Admin
        User admin = new User(
                "admin@smartplacement.com",
                passwordEncoder.encode("Admin@123"),
                Role.ROLE_TPO_ADMIN,
                UserStatus.ACTIVE
        );
        userRepository.save(admin);
    }

    private String obtainToken(String email, String password) throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto(email, password);
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        AuthResponseDto authResponse = objectMapper.readValue(
                objectMapper.readTree(responseBody).get("data").toString(),
                AuthResponseDto.class
        );
        return authResponse.getAccessToken();
    }

    private String registerRecruiterAndGetToken(String email, String companyName) throws Exception {
        RecruiterRegisterRequestDto request = new RecruiterRegisterRequestDto();
        request.setEmail(email);
        request.setPassword("Secret@123");
        request.setCompanyName(companyName);
        request.setDesignation("Lead Talent Partner");
        request.setPhone("+919876543210");

        mockMvc.perform(post("/api/v1/auth/recruiter/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Company company = companyRepository.findByNameIgnoreCase(companyName).orElseThrow();
        company.setVerified(true);
        companyRepository.save(company);

        return obtainToken(email, "Secret@123");
    }

    private String registerStudentAndGetToken(String email, String rollNumber, String branch,
                                              Integer gradYear, Double cgpa, boolean withResume,
                                              String... skills) throws Exception {
        StudentRegisterRequestDto request = new StudentRegisterRequestDto();
        request.setEmail(email);
        request.setPassword("Secret@123");
        request.setRollNumber(rollNumber);
        request.setFirstName("Pooja");
        request.setLastName("Iyer");
        request.setBranch(branch);
        request.setGraduationYear(gradYear);
        request.setCgpa(cgpa);
        request.setPhone("+919876543222");

        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Student student = studentRepository.findByRollNumber(rollNumber).orElseThrow();
        student.setActiveBacklogs(0);
        student.setTenthPercentage(88.0);
        student.setTwelfthPercentage(84.0);
        student.setGapYears(0);

        if (withResume) {
            student.setResumeUrl("/uploads/resumes/test_resume_" + rollNumber + ".pdf");
            student.setResumeFilename("test_resume_" + rollNumber + ".pdf");
        }

        Student saved = studentRepository.save(student);

        for (String skill : skills) {
            skillRepository.save(new StudentSkill(saved, skill, SkillProficiency.ADVANCED));
        }

        return obtainToken(email, "Secret@123");
    }

    private Long createJob(String recruiterToken, Double minCgpa, String branch, Integer gradYear, String... requiredSkills) throws Exception {
        JobCreateRequestDto request = new JobCreateRequestDto();
        request.setTitle("Cloud Systems Engineer");
        request.setDescription("Design resilient distributed cloud systems.");
        request.setJobType(JobType.FULL_TIME);
        request.setLocation("Hyderabad");
        request.setSalaryPackageLpa(18.0);
        request.setApplicationDeadline(LocalDateTime.now().plusDays(10));
        request.setDriveDate(LocalDate.now().plusDays(20));
        request.setStatus(JobStatus.PUBLISHED);

        EligibilityCriteriaDto criteria = new EligibilityCriteriaDto();
        criteria.setMinCgpa(minCgpa);
        criteria.setMaxActiveBacklogs(0);
        criteria.setMaxHistoryBacklogs(1);
        criteria.setMinTenthPercentage(70.0);
        criteria.setMinTwelfthPercentage(70.0);
        criteria.setMaxGapYears(1);
        criteria.setAllowedBranches(Arrays.asList(branch));
        criteria.setAllowedGradYears(Arrays.asList(gradYear));
        criteria.setRequiredSkills(Arrays.asList(requiredSkills));
        request.setEligibilityCriteria(criteria);

        MvcResult result = mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    @Test
    @DisplayName("Student without resume on file should be rejected with 400 when applying")
    void studentWithoutResumeCannotApply() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@uber.com", "Uber");
        Long jobId = createJob(recruiterToken, 7.0, "Computer Science and Engineering", 2026, "Java");

        String studentToken = registerStudentAndGetToken("noresume@univ.edu", "2024CS050", "Computer Science and Engineering", 2026, 8.5, false, "Java");

        mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("You must upload a resume")));
    }

    @Test
    @DisplayName("Eligible student with resume should successfully apply with status APPLIED")
    void eligibleStudentWithResumeCanApplySuccessfully() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@salesforce.com", "Salesforce");
        Long jobId = createJob(recruiterToken, 7.5, "Computer Science and Engineering", 2026, "Java", "Cloud");

        String studentToken = registerStudentAndGetToken(
                "eligible@univ.edu", "2024CS051", "Computer Science and Engineering",
                2026, 8.8, true, "Java", "Cloud"
        );

        mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("APPLIED")))
                .andExpect(jsonPath("$.data.jobTitle", is("Cloud Systems Engineer")))
                .andExpect(jsonPath("$.data.companyName", is("Salesforce")))
                .andExpect(jsonPath("$.data.resumeSnapshotUrl", containsString("test_resume_2024CS051.pdf")));
    }

    @Test
    @DisplayName("Ineligible student should be blocked with 400 and explicit rejection reasons")
    void ineligibleStudentCannotApply() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@oracle.com", "Oracle");
        Long jobId = createJob(recruiterToken, 8.0, "Computer Science and Engineering", 2026, "Java");

        // Ineligible: CGPA 7.10 (< 8.00)
        String studentToken = registerStudentAndGetToken(
                "ineligible@univ.edu", "2024CS052", "Computer Science and Engineering",
                2026, 7.10, true, "Java"
        );

        mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("You do not meet the mandatory eligibility criteria")))
                .andExpect(jsonPath("$.message", containsString("Minimum CGPA required is 8.00")));
    }

    @Test
    @DisplayName("Duplicate application submission should be prevented with 400 Bad Request")
    void duplicateApplicationIsBlocked() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@cisco.com", "Cisco");
        Long jobId = createJob(recruiterToken, 7.0, "Computer Science and Engineering", 2026, "Java");

        String studentToken = registerStudentAndGetToken(
                "dup@univ.edu", "2024CS053", "Computer Science and Engineering",
                2026, 8.0, true, "Java"
        );

        // First application passes
        mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isCreated());

        // Second application fails
        mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("You have already submitted an application")));
    }

    @Test
    @DisplayName("Student can view their own personal applications pipeline")
    void studentCanViewPersonalApplications() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@intel.com", "Intel");
        Long jobId = createJob(recruiterToken, 7.0, "Computer Science and Engineering", 2026, "Java");

        String studentToken = registerStudentAndGetToken(
                "viewer@univ.edu", "2024CS054", "Computer Science and Engineering",
                2026, 8.2, true, "Java"
        );

        mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/applications/my")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].companyName", is("Intel")));
    }

    @Test
    @DisplayName("Recruiter can view applicant pipeline for their job drive")
    void recruiterCanViewJobApplicants() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@nvidia.com", "Nvidia");
        Long jobId = createJob(recruiterToken, 7.0, "Computer Science and Engineering", 2026, "Java");

        String studentToken = registerStudentAndGetToken(
                "cand@univ.edu", "2024CS055", "Computer Science and Engineering",
                2026, 8.4, true, "Java"
        );

        mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/applications/jobs/" + jobId)
                        .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].studentRollNumber", is("2024CS055")));
    }

    @Test
    @DisplayName("Recruiter can transition application status to SHORTLISTED")
    void recruiterCanTransitionStatus() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@ibm.com", "IBM");
        Long jobId = createJob(recruiterToken, 7.0, "Computer Science and Engineering", 2026, "Java");

        String studentToken = registerStudentAndGetToken(
                "advance@univ.edu", "2024CS056", "Computer Science and Engineering",
                2026, 8.7, true, "Java"
        );

        MvcResult appResult = mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isCreated())
                .andReturn();

        Long appId = objectMapper.readTree(appResult.getResponse().getContentAsString()).get("data").get("id").asLong();

        ApplicationStatusUpdateDto updateReq = new ApplicationStatusUpdateDto(
                ApplicationStatus.SHORTLISTED,
                "Shortlisted for Technical Assessment",
                null
        );

        mockMvc.perform(put("/api/v1/applications/" + appId + "/status")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("SHORTLISTED")))
                .andExpect(jsonPath("$.data.currentRound", is("Shortlisted for Technical Assessment")));
    }

    @Test
    @DisplayName("Recruiter can bulk transition candidate applications in batch")
    void recruiterCanBatchUpdateApplications() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@goldman.com", "Goldman Sachs");
        Long jobId = createJob(recruiterToken, 7.0, "Computer Science and Engineering", 2026, "Java");

        String s1Token = registerStudentAndGetToken("b1@univ.edu", "2024CS061", "Computer Science and Engineering", 2026, 8.5, true, "Java");
        String s2Token = registerStudentAndGetToken("b2@univ.edu", "2024CS062", "Computer Science and Engineering", 2026, 8.6, true, "Java");

        MvcResult r1 = mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply").header("Authorization", "Bearer " + s1Token)).andReturn();
        MvcResult r2 = mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply").header("Authorization", "Bearer " + s2Token)).andReturn();

        Long id1 = objectMapper.readTree(r1.getResponse().getContentAsString()).get("data").get("id").asLong();
        Long id2 = objectMapper.readTree(r2.getResponse().getContentAsString()).get("data").get("id").asLong();

        BatchApplicationStatusUpdateDto batchReq = new BatchApplicationStatusUpdateDto(
                Arrays.asList(id1, id2),
                ApplicationStatus.SHORTLISTED,
                "Online Assessment Round Scheduled",
                null
        );

        mockMvc.perform(post("/api/v1/applications/batch-status")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(batchReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].status", is("SHORTLISTED")))
                .andExpect(jsonPath("$.data[1].status", is("SHORTLISTED")));
    }

    @Test
    @DisplayName("Student can withdraw their application while it remains in APPLIED status")
    void studentCanWithdrawApplication() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@qualcomm.com", "Qualcomm");
        Long jobId = createJob(recruiterToken, 7.0, "Computer Science and Engineering", 2026, "Java");

        String studentToken = registerStudentAndGetToken(
                "withdraw@univ.edu", "2024CS070", "Computer Science and Engineering",
                2026, 8.3, true, "Java"
        );

        MvcResult appResult = mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isCreated())
                .andReturn();

        Long appId = objectMapper.readTree(appResult.getResponse().getContentAsString()).get("data").get("id").asLong();

        mockMvc.perform(delete("/api/v1/applications/" + appId + "/withdraw")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("withdrawn successfully")));

        Application app = applicationRepository.findById(appId).orElseThrow();
        assertEquals(ApplicationStatus.WITHDRAWN, app.getStatus());
    }

    @Test
    @DisplayName("Student cannot withdraw application once it has advanced to SHORTLISTED status")
    void studentCannotWithdrawShortlistedApplication() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@spotify.com", "Spotify");
        Long jobId = createJob(recruiterToken, 7.0, "Computer Science and Engineering", 2026, "Java");

        String studentToken = registerStudentAndGetToken(
                "locked@univ.edu", "2024CS071", "Computer Science and Engineering",
                2026, 8.9, true, "Java"
        );

        MvcResult appResult = mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isCreated())
                .andReturn();

        Long appId = objectMapper.readTree(appResult.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Advance to SHORTLISTED
        ApplicationStatusUpdateDto updateReq = new ApplicationStatusUpdateDto(ApplicationStatus.SHORTLISTED, "OA Scheduled", null);
        mockMvc.perform(put("/api/v1/applications/" + appId + "/status")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk());

        // Attempt withdrawal
        mockMvc.perform(delete("/api/v1/applications/" + appId + "/withdraw")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("cannot be withdrawn once it has progressed beyond APPLIED stage")));
    }
}
