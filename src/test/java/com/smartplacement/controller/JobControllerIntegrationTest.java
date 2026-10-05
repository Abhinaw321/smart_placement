package com.smartplacement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartplacement.dto.auth.AuthResponseDto;
import com.smartplacement.dto.auth.LoginRequestDto;
import com.smartplacement.dto.auth.RecruiterRegisterRequestDto;
import com.smartplacement.dto.auth.StudentRegisterRequestDto;
import com.smartplacement.dto.job.EligibilityCriteriaDto;
import com.smartplacement.dto.job.JobCreateRequestDto;
import com.smartplacement.dto.job.JobUpdateRequestDto;
import com.smartplacement.entity.Company;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.SkillProficiency;
import com.smartplacement.entity.Student;
import com.smartplacement.entity.StudentSkill;
import com.smartplacement.entity.User;
import com.smartplacement.entity.UserStatus;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentSkillRepository skillRepository;

    @Autowired
    private StudentProjectRepository projectRepository;

    @Autowired
    private StudentCertificationRepository certificationRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private com.smartplacement.repository.AuditLogRepository auditLogRepository;

    @Autowired
    private com.smartplacement.repository.NotificationRepository notificationRepository;

    @Autowired
    private com.smartplacement.repository.JobOfferRepository jobOfferRepository;

    @Autowired
    private com.smartplacement.repository.InterviewRepository interviewRepository;

    @Autowired
    private com.smartplacement.repository.ApplicationRepository applicationRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EligibilityCriteriaRepository eligibilityCriteriaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        auditLogRepository.deleteAll();
        notificationRepository.deleteAll();
        jobOfferRepository.deleteAll();
        interviewRepository.deleteAll();
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

        // Ensure default TPO Admin exists
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

    private String registerRecruiterAndGetToken(String email, String companyName, boolean verifyCompany) throws Exception {
        RecruiterRegisterRequestDto request = new RecruiterRegisterRequestDto();
        request.setEmail(email);
        request.setPassword("Secret@123");
        request.setCompanyName(companyName);
        request.setDesignation("Lead Technical Recruiter");
        request.setPhone("+919876543210");

        mockMvc.perform(post("/api/v1/auth/recruiter/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        if (verifyCompany) {
            Company company = companyRepository.findByNameIgnoreCase(companyName).orElseThrow();
            company.setVerified(true);
            companyRepository.save(company);
        }

        return obtainToken(email, "Secret@123");
    }

    private String registerStudentAndGetToken(String email, String rollNumber, String branch,
                                              Integer gradYear, Double cgpa, Integer backlogs,
                                              String... skills) throws Exception {
        StudentRegisterRequestDto request = new StudentRegisterRequestDto();
        request.setEmail(email);
        request.setPassword("Secret@123");
        request.setRollNumber(rollNumber);
        request.setFirstName("Alex");
        request.setLastName("Morgan");
        request.setBranch(branch);
        request.setGraduationYear(gradYear);
        request.setCgpa(cgpa);
        request.setPhone("+919876543211");

        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Student student = studentRepository.findByRollNumber(rollNumber).orElseThrow();
        student.setActiveBacklogs(backlogs);
        student.setTenthPercentage(90.0);
        student.setTwelfthPercentage(85.0);
        Student savedStudent = studentRepository.save(student);

        for (String skillName : skills) {
            skillRepository.save(new StudentSkill(savedStudent, skillName, SkillProficiency.ADVANCED));
        }

        return obtainToken(email, "Secret@123");
    }

    private JobCreateRequestDto createStandardJobRequest() {
        JobCreateRequestDto request = new JobCreateRequestDto();
        request.setTitle("Software Engineer - Backend");
        request.setDescription("Design and build scalable microservices using Java and Spring Boot.");
        request.setJobType(JobType.FULL_TIME);
        request.setLocation("Bangalore");
        request.setSalaryPackageLpa(16.5);
        request.setApplicationDeadline(LocalDateTime.now().plusDays(14));
        request.setDriveDate(LocalDate.now().plusDays(21));
        request.setStatus(JobStatus.PUBLISHED);

        EligibilityCriteriaDto criteria = new EligibilityCriteriaDto();
        criteria.setMinCgpa(7.5);
        criteria.setMaxActiveBacklogs(0);
        criteria.setMaxHistoryBacklogs(1);
        criteria.setMinTenthPercentage(70.0);
        criteria.setMinTwelfthPercentage(70.0);
        criteria.setMaxGapYears(1);
        criteria.setAllowedBranches(Arrays.asList("Computer Science and Engineering", "Information Technology"));
        criteria.setAllowedGradYears(Arrays.asList(2026));
        criteria.setRequiredSkills(Arrays.asList("Java", "Spring Boot"));

        request.setEligibilityCriteria(criteria);
        return request;
    }

    @Test
    @DisplayName("Recruiter of verified company should successfully post a job with criteria")
    void recruiterOfVerifiedCompanyCanPostJob() throws Exception {
        String token = registerRecruiterAndGetToken("recruiter@google.com", "Google India", true);
        JobCreateRequestDto request = createStandardJobRequest();

        mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Software Engineer - Backend")))
                .andExpect(jsonPath("$.data.companyName", is("Google India")))
                .andExpect(jsonPath("$.data.salaryPackageLpa", is(16.5)))
                .andExpect(jsonPath("$.data.eligibilityCriteria.minCgpa", is(7.5)))
                .andExpect(jsonPath("$.data.eligibilityCriteria.allowedBranches", hasSize(2)));
    }

    @Test
    @DisplayName("Recruiter of unverified company should be rejected with 400 when attempting to post a job")
    void unverifiedRecruiterCannotPostJob() throws Exception {
        String token = registerRecruiterAndGetToken("recruiter@startup.io", "Unverified Startup", false);
        JobCreateRequestDto request = createStandardJobRequest();

        mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("has not been verified by the TPO yet")));
    }

    @Test
    @DisplayName("Student should be denied with 403 Forbidden when attempting to create a job")
    void studentCannotCreateJob() throws Exception {
        String token = registerStudentAndGetToken("student@univ.edu", "2024CS001", "Computer Science and Engineering", 2026, 8.5, 0);
        JobCreateRequestDto request = createStandardJobRequest();

        mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Recruiter can update job details and eligibility criteria")
    void recruiterCanUpdateJob() throws Exception {
        String token = registerRecruiterAndGetToken("recruiter@microsoft.com", "Microsoft", true);
        JobCreateRequestDto createReq = createStandardJobRequest();

        MvcResult result = mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long jobId = objectMapper.readTree(result.getResponse().getContentAsString()).get("data").get("id").asLong();

        JobUpdateRequestDto updateReq = new JobUpdateRequestDto();
        updateReq.setTitle("Senior Backend Engineer");
        updateReq.setSalaryPackageLpa(22.0);

        mockMvc.perform(put("/api/v1/jobs/" + jobId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("Senior Backend Engineer")))
                .andExpect(jsonPath("$.data.salaryPackageLpa", is(22.0)));
    }

    @Test
    @DisplayName("TPO Admin can post a job on behalf of any company")
    void tpoAdminCanPostJobForCompany() throws Exception {
        String adminToken = obtainToken("admin@smartplacement.com", "Admin@123");

        Company company = new Company("Amazon AWS", "Cloud Computing", "https://aws.amazon.com", "Cloud", null, "Seattle");
        company.setVerified(true);
        Company savedCompany = companyRepository.save(company);

        JobCreateRequestDto request = createStandardJobRequest();
        request.setCompanyId(savedCompany.getId());

        mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.companyName", is("Amazon AWS")));
    }

    @Test
    @DisplayName("Student can check real-time eligibility: Eligible student passes with empty rejection list")
    void eligibleStudentPassesEligibilityCheck() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@meta.com", "Meta", true);
        JobCreateRequestDto jobReq = createStandardJobRequest();

        MvcResult jobResult = mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long jobId = objectMapper.readTree(jobResult.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Candidate has 8.5 CGPA, CSE branch, 2026 batch, 0 backlogs, and Java & Spring Boot skills
        String studentToken = registerStudentAndGetToken(
                "eligible@univ.edu", "2024CS002", "Computer Science and Engineering",
                2026, 8.5, 0, "Java", "Spring Boot"
        );

        mockMvc.perform(get("/api/v1/jobs/" + jobId + "/my-eligibility")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.eligible", is(true)))
                .andExpect(jsonPath("$.data.rejectionReasons", hasSize(0)))
                .andExpect(jsonPath("$.data.criterionResults", hasSize(7)));
    }

    @Test
    @DisplayName("Student can check real-time eligibility: Ineligible student receives explicit failure reasons")
    void ineligibleStudentReceivesExplicitReasons() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@netflix.com", "Netflix", true);
        JobCreateRequestDto jobReq = createStandardJobRequest();

        MvcResult jobResult = mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long jobId = objectMapper.readTree(jobResult.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Ineligible: 6.8 CGPA (< 7.5), Mechanical branch (not CSE/IT), 1 active backlog (> 0), missing Spring Boot skill
        String studentToken = registerStudentAndGetToken(
                "ineligible@univ.edu", "2024ME001", "Mechanical Engineering",
                2026, 6.8, 1, "Java"
        );

        mockMvc.perform(get("/api/v1/jobs/" + jobId + "/my-eligibility")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.eligible", is(false)))
                .andExpect(jsonPath("$.data.rejectionReasons", hasSize(4))); // CGPA, Backlogs, Branch, Missing skill
    }

    @Test
    @DisplayName("Recruiter can query pool of eligible students across the institution for their job drive")
    void recruiterCanQueryEligibleCandidatePool() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@apple.com", "Apple", true);
        JobCreateRequestDto jobReq = createStandardJobRequest();

        MvcResult jobResult = mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long jobId = objectMapper.readTree(jobResult.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Seed 1 eligible student and 1 ineligible student
        registerStudentAndGetToken("qual@univ.edu", "2024CS010", "Computer Science and Engineering", 2026, 8.8, 0, "Java", "Spring Boot");
        registerStudentAndGetToken("disqual@univ.edu", "2024CS011", "Computer Science and Engineering", 2026, 6.2, 0, "Java", "Spring Boot");

        mockMvc.perform(get("/api/v1/jobs/" + jobId + "/eligible-candidates")
                        .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].rollNumber", is("2024CS010")));
    }

    @Test
    @DisplayName("Recruiter can transition job status e.g. from PUBLISHED to CLOSED")
    void recruiterCanTransitionJobStatus() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@adobe.com", "Adobe", true);
        JobCreateRequestDto jobReq = createStandardJobRequest();

        MvcResult jobResult = mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long jobId = objectMapper.readTree(jobResult.getResponse().getContentAsString()).get("data").get("id").asLong();

        mockMvc.perform(patch("/api/v1/jobs/" + jobId + "/status")
                        .param("status", "CLOSED")
                        .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/jobs/" + jobId)
                        .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("CLOSED")));
    }
}
