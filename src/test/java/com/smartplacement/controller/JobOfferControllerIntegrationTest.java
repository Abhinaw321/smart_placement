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
import com.smartplacement.entity.Application;
import com.smartplacement.entity.ApplicationStatus;
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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JobOfferControllerIntegrationTest {

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
        request.setDesignation("Director of Talent Acquisition");
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
        request.setFirstName("Rohan");
        request.setLastName("Deshmukh");
        request.setBranch("Computer Science and Engineering");
        request.setGraduationYear(2026);
        request.setCgpa(9.2);
        request.setPhone("+919876543333");

        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Student student = studentRepository.findByRollNumber(rollNumber).orElseThrow();
        student.setActiveBacklogs(0);
        student.setTenthPercentage(95.0);
        student.setTwelfthPercentage(92.0);
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
        request.setTitle("Member of Technical Staff");
        request.setDescription("Design distributed cloud applications.");
        request.setJobType(JobType.FULL_TIME);
        request.setLocation("Bengaluru");
        request.setSalaryPackageLpa(28.0);
        request.setApplicationDeadline(LocalDateTime.now().plusDays(10));
        request.setDriveDate(LocalDate.now().plusDays(15));
        request.setStatus(JobStatus.PUBLISHED);

        EligibilityCriteriaDto criteria = new EligibilityCriteriaDto();
        criteria.setMinCgpa(7.5);
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
    @DisplayName("Recruiter can issue an employment offer, advancing application status and alerting candidate")
    void recruiterCanIssueOffer() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@google.com", "Google");
        String studentToken = registerStudentAndGetToken("student1@univ.edu", "2024CS201");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        OfferIssueRequestDto issueReq = new OfferIssueRequestDto();
        issueReq.setApplicationId(appId);
        issueReq.setCtcLpa(32.0);
        issueReq.setDesignation("Software Engineer I");
        issueReq.setValidUntil(LocalDate.now().plusDays(7));
        issueReq.setJoiningDate(LocalDate.now().plusMonths(6));
        issueReq.setOfferLetterUrl("https://storage.univ.edu/offers/offer_2024CS201.pdf");
        issueReq.setNotes("Congratulations on passing all technical rounds with exemplary performance!");

        mockMvc.perform(post("/api/v1/offers/issue")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.designation", is("Software Engineer I")))
                .andExpect(jsonPath("$.data.ctcLpa", is(32.0)))
                .andExpect(jsonPath("$.data.status", is("PENDING")))
                .andExpect(jsonPath("$.data.companyName", is("Google")));

        Application app = applicationRepository.findByIdWithDetails(appId).orElseThrow();
        assertEquals(ApplicationStatus.OFFER_EXTENDED, app.getStatus());

        // Verify in-app notification delivered
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(app.getStudent().getUser().getId());
        assertEquals(1, unreadCount);

        // Verify audit log recorded
        long auditCount = auditLogRepository.count();
        assertTrue(auditCount >= 1);
    }

    @Test
    @DisplayName("Student can accept an offer: locks placement status to placed and updates application")
    void studentCanAcceptOffer() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@microsoft.com", "Microsoft");
        String studentToken = registerStudentAndGetToken("student2@univ.edu", "2024CS202");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        OfferIssueRequestDto issueReq = new OfferIssueRequestDto();
        issueReq.setApplicationId(appId);
        issueReq.setCtcLpa(25.0);
        issueReq.setDesignation("Software Development Engineer");
        issueReq.setValidUntil(LocalDate.now().plusDays(5));

        MvcResult offerRes = mockMvc.perform(post("/api/v1/offers/issue")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long offerId = objectMapper.readTree(offerRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        OfferResponseRequestDto responseDto = new OfferResponseRequestDto(
                OfferStatus.ACCEPTED,
                "Delighted to accept the offer! Thank you to the entire recruitment team."
        );

        mockMvc.perform(put("/api/v1/offers/" + offerId + "/respond")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("ACCEPTED")));

        Application app = applicationRepository.findById(appId).orElseThrow();
        assertEquals(ApplicationStatus.OFFER_ACCEPTED, app.getStatus());

        Student student = studentRepository.findByRollNumber("2024CS202").orElseThrow();
        assertTrue(student.getIsPlaced(), "Student should be locked as placed");
    }

    @Test
    @DisplayName("Student can decline an offer: transitions application to declined state")
    void studentCanDeclineOffer() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@amazon.com", "Amazon");
        String studentToken = registerStudentAndGetToken("student3@univ.edu", "2024CS203");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        OfferIssueRequestDto issueReq = new OfferIssueRequestDto();
        issueReq.setApplicationId(appId);
        issueReq.setCtcLpa(22.0);
        issueReq.setDesignation("Quality Assurance Engineer");
        issueReq.setValidUntil(LocalDate.now().plusDays(4));

        MvcResult offerRes = mockMvc.perform(post("/api/v1/offers/issue")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long offerId = objectMapper.readTree(offerRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        OfferResponseRequestDto responseDto = new OfferResponseRequestDto(
                OfferStatus.DECLINED,
                "Pursuing higher education abroad."
        );

        mockMvc.perform(put("/api/v1/offers/" + offerId + "/respond")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("DECLINED")));

        Application app = applicationRepository.findById(appId).orElseThrow();
        assertEquals(ApplicationStatus.OFFER_DECLINED, app.getStatus());
    }

    @Test
    @DisplayName("Recruiter can revoke an offer before student acceptance")
    void recruiterCanRevokeOffer() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@apple.com", "Apple");
        String studentToken = registerStudentAndGetToken("student4@univ.edu", "2024CS204");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        OfferIssueRequestDto issueReq = new OfferIssueRequestDto();
        issueReq.setApplicationId(appId);
        issueReq.setCtcLpa(30.0);
        issueReq.setDesignation("Firmware Engineer");
        issueReq.setValidUntil(LocalDate.now().plusDays(5));

        MvcResult offerRes = mockMvc.perform(post("/api/v1/offers/issue")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long offerId = objectMapper.readTree(offerRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        mockMvc.perform(put("/api/v1/offers/" + offerId + "/revoke")
                        .param("reason", "Project headcount reallocation")
                        .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("REVOKED")));

        Application app = applicationRepository.findById(appId).orElseThrow();
        assertEquals(ApplicationStatus.REJECTED, app.getStatus());
    }

    @Test
    @DisplayName("Student can view received offers and acknowledge notifications")
    void studentCanViewOffersAndNotifications() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@meta.com", "Meta");
        String studentToken = registerStudentAndGetToken("student5@univ.edu", "2024CS205");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        OfferIssueRequestDto issueReq = new OfferIssueRequestDto();
        issueReq.setApplicationId(appId);
        issueReq.setCtcLpa(35.0);
        issueReq.setDesignation("Production Engineer");
        issueReq.setValidUntil(LocalDate.now().plusDays(7));

        mockMvc.perform(post("/api/v1/offers/issue")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueReq)))
                .andExpect(status().isCreated());

        // Check offers endpoint
        mockMvc.perform(get("/api/v1/offers/my")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].companyName", is("Meta")));

        // Check notifications endpoint
        mockMvc.perform(get("/api/v1/notifications")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].type", is("OFFER_RECEIVED")));

        // Mark all as read
        mockMvc.perform(put("/api/v1/notifications/read-all")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(0)));
    }

    @Test
    @DisplayName("TPO Admin can view compliance audit logs")
    void adminCanViewAuditLogs() throws Exception {
        String adminToken = obtainToken("admin@smartplacement.com", "Admin@123");
        String recruiterToken = registerRecruiterAndGetToken("recruiter@adobe.com", "Adobe");
        String studentToken = registerStudentAndGetToken("student6@univ.edu", "2024CS206");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        OfferIssueRequestDto issueReq = new OfferIssueRequestDto();
        issueReq.setApplicationId(appId);
        issueReq.setCtcLpa(20.0);
        issueReq.setDesignation("Software Engineer");
        issueReq.setValidUntil(LocalDate.now().plusDays(6));

        mockMvc.perform(post("/api/v1/offers/issue")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueReq)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/audit-logs")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Student is forbidden from issuing job offers")
    void studentForbiddenFromIssuingOffer() throws Exception {
        String studentToken = registerStudentAndGetToken("student7@univ.edu", "2024CS207");

        OfferIssueRequestDto issueReq = new OfferIssueRequestDto();
        issueReq.setApplicationId(999L);
        issueReq.setCtcLpa(10.0);
        issueReq.setDesignation("Hacker");
        issueReq.setValidUntil(LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/v1/offers/issue")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(issueReq)))
                .andExpect(status().isForbidden());
    }
}
