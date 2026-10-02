package com.smartplacement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartplacement.dto.auth.AuthResponseDto;
import com.smartplacement.dto.auth.LoginRequestDto;
import com.smartplacement.dto.auth.RecruiterRegisterRequestDto;
import com.smartplacement.dto.auth.StudentRegisterRequestDto;
import com.smartplacement.dto.job.EligibilityCriteriaDto;
import com.smartplacement.dto.job.JobCreateRequestDto;
import com.smartplacement.dto.offer.OfferIssueRequestDto;
import com.smartplacement.dto.offer.OfferResponseRequestDto;
import com.smartplacement.entity.Company;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import com.smartplacement.entity.OfferStatus;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.SkillProficiency;
import com.smartplacement.entity.Student;
import com.smartplacement.entity.StudentSkill;
import com.smartplacement.entity.User;
import com.smartplacement.entity.UserStatus;
import com.smartplacement.repository.ApplicationRepository;
import com.smartplacement.repository.AuditLogRepository;
import com.smartplacement.repository.CompanyRepository;
import com.smartplacement.repository.EligibilityCriteriaRepository;
import com.smartplacement.repository.InterviewRepository;
import com.smartplacement.repository.JobOfferRepository;
import com.smartplacement.repository.JobRepository;
import com.smartplacement.repository.NotificationRepository;
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
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AnalyticsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private InterviewRepository interviewRepository;

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

        // Reseed default TPO Admin
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
        request.setDesignation("VP University Relations");
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

    private String registerStudentAndGetToken(String email, String rollNumber) throws Exception {
        StudentRegisterRequestDto request = new StudentRegisterRequestDto();
        request.setEmail(email);
        request.setPassword("Secret@123");
        request.setRollNumber(rollNumber);
        request.setFirstName("Vikram");
        request.setLastName("Sethi");
        request.setBranch("Computer Science and Engineering");
        request.setGraduationYear(2026);
        request.setCgpa(8.8);
        request.setPhone("+919876543333");

        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Student student = studentRepository.findByRollNumber(rollNumber).orElseThrow();
        student.setActiveBacklogs(0);
        student.setTenthPercentage(91.0);
        student.setTwelfthPercentage(89.0);
        student.setGapYears(0);
        student.setResumeUrl("/uploads/resumes/resume_" + rollNumber + ".pdf");
        student.setResumeFilename("resume_" + rollNumber + ".pdf");
        Student saved = studentRepository.save(student);

        skillRepository.save(new StudentSkill(saved, "Java", SkillProficiency.ADVANCED));
        skillRepository.save(new StudentSkill(saved, "Spring Boot", SkillProficiency.ADVANCED));

        return obtainToken(email, "Secret@123");
    }

    private Long createJobAndApply(String recruiterToken, String studentToken) throws Exception {
        JobCreateRequestDto request = new JobCreateRequestDto();
        request.setTitle("Cloud Systems Engineer");
        request.setDescription("Design large-scale distributed systems.");
        request.setJobType(JobType.FULL_TIME);
        request.setLocation("Hyderabad");
        request.setSalaryPackageLpa(24.0);
        request.setApplicationDeadline(LocalDateTime.now().plusDays(10));
        request.setDriveDate(LocalDate.now().plusDays(15));
        request.setStatus(JobStatus.PUBLISHED);

        EligibilityCriteriaDto criteria = new EligibilityCriteriaDto();
        criteria.setMinCgpa(7.0);
        criteria.setMaxActiveBacklogs(0);
        criteria.setAllowedBranches(Collections.singletonList("Computer Science and Engineering"));
        criteria.setAllowedGradYears(Collections.singletonList(2026));
        criteria.setRequiredSkills(Collections.singletonList("Java"));
        request.setEligibilityCriteria(criteria);

        MvcResult jobRes = mockMvc.perform(post("/api/v1/jobs")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Long jobId = objectMapper.readTree(jobRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        MvcResult appRes = mockMvc.perform(post("/api/v1/applications/jobs/" + jobId + "/apply")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(appRes.getResponse().getContentAsString()).get("data").get("id").asLong();
    }

    @Test
    @DisplayName("Student can view personalized dashboard metrics")
    void studentCanViewDashboard() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@salesforce.com", "Salesforce");
        String studentToken = registerStudentAndGetToken("student1@univ.edu", "2024CS301");
        createJobAndApply(recruiterToken, studentToken);

        mockMvc.perform(get("/api/v1/analytics/student-dashboard")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalApplicationsSubmitted", is(1)))
                .andExpect(jsonPath("$.data.isPlaced", is(false)))
                .andExpect(jsonPath("$.data.profileCompletionPercentage", greaterThanOrEqualTo(50)));
    }

    @Test
    @DisplayName("Recruiter can view hiring pipeline metrics")
    void recruiterCanViewDashboard() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@oracle.com", "Oracle");
        String studentToken = registerStudentAndGetToken("student2@univ.edu", "2024CS302");
        createJobAndApply(recruiterToken, studentToken);

        mockMvc.perform(get("/api/v1/analytics/recruiter-dashboard")
                        .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.companyName", is("Oracle")))
                .andExpect(jsonPath("$.data.totalJobsPosted", is(1)))
                .andExpect(jsonPath("$.data.activeDrivesCount", is(1)))
                .andExpect(jsonPath("$.data.totalApplicationsReceived", is(1)));
    }

    @Test
    @DisplayName("TPO Admin can view institutional placement KPIs and salary distributions")
    void tpoAdminCanViewExecutiveDashboard() throws Exception {
        String adminToken = obtainToken("admin@smartplacement.com", "Admin@123");
        String recruiterToken = registerRecruiterAndGetToken("recruiter@cisco.com", "Cisco");
        String studentToken = registerStudentAndGetToken("student3@univ.edu", "2024CS303");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        // Issue and accept offer
        OfferIssueRequestDto issueReq = new OfferIssueRequestDto();
        issueReq.setApplicationId(appId);
        issueReq.setCtcLpa(18.5);
        issueReq.setDesignation("Software Engineer");
        issueReq.setValidUntil(LocalDate.now().plusDays(5));

        MvcResult offerRes = mockMvc.perform(post("/api/v1/offers/issue")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long offerId = objectMapper.readTree(offerRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        OfferResponseRequestDto responseDto = new OfferResponseRequestDto(OfferStatus.ACCEPTED, "Accepting!");
        mockMvc.perform(put("/api/v1/offers/" + offerId + "/respond")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseDto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/analytics/tpo-dashboard")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalRegisteredStudents", is(1)))
                .andExpect(jsonPath("$.data.totalPlacedStudents", is(1)))
                .andExpect(jsonPath("$.data.overallPlacementPercentage", is(100.0)))
                .andExpect(jsonPath("$.data.totalOffersAccepted", is(1)))
                .andExpect(jsonPath("$.data.highestPackageLpa", is(18.5)))
                .andExpect(jsonPath("$.data.averagePackageLpa", is(18.5)))
                .andExpect(jsonPath("$.data.departmentStats", hasSize(1)))
                .andExpect(jsonPath("$.data.salaryDistribution.tier3Between12And20Lpa", is(1)));
    }

    @Test
    @DisplayName("TPO Admin can export placement master sheet as RFC 4180 CSV")
    void tpoAdminCanExportPlacementsCsv() throws Exception {
        String adminToken = obtainToken("admin@smartplacement.com", "Admin@123");
        String recruiterToken = registerRecruiterAndGetToken("recruiter@intel.com", "Intel");
        String studentToken = registerStudentAndGetToken("student4@univ.edu", "2024CS304");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        OfferIssueRequestDto issueReq = new OfferIssueRequestDto();
        issueReq.setApplicationId(appId);
        issueReq.setCtcLpa(21.0);
        issueReq.setDesignation("Silicon Design Engineer");
        issueReq.setValidUntil(LocalDate.now().plusDays(5));

        mockMvc.perform(post("/api/v1/offers/issue")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueReq)))
                .andExpect(status().isCreated());

        MvcResult csvResult = mockMvc.perform(get("/api/v1/analytics/export/placements")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("placements_master_report.csv")))
                .andExpect(content().contentType("text/csv; charset=UTF-8"))
                .andReturn();

        String csvContent = csvResult.getResponse().getContentAsString();
        assertTrue(csvContent.contains("Offer ID,Roll Number,Student Name"));
        assertTrue(csvContent.contains("2024CS304"));
        assertTrue(csvContent.contains("Intel"));
        assertTrue(csvContent.contains("Silicon Design Engineer"));
    }

    @Test
    @DisplayName("Student is forbidden from viewing executive TPO dashboard and CSV export")
    void studentForbiddenFromTpoDashboard() throws Exception {
        String studentToken = registerStudentAndGetToken("student5@univ.edu", "2024CS305");

        mockMvc.perform(get("/api/v1/analytics/tpo-dashboard")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/analytics/export/placements")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Recruiter is forbidden from accessing student dashboard")
    void recruiterForbiddenFromStudentDashboard() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@dell.com", "Dell");

        mockMvc.perform(get("/api/v1/analytics/student-dashboard")
                        .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isForbidden());
    }
}
