package com.smartplacement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartplacement.dto.application.ApplicationResponseDto;
import com.smartplacement.dto.auth.AuthResponseDto;
import com.smartplacement.dto.auth.LoginRequestDto;
import com.smartplacement.dto.auth.RecruiterRegisterRequestDto;
import com.smartplacement.dto.auth.StudentRegisterRequestDto;
import com.smartplacement.dto.interview.InterviewResponseDto;
import com.smartplacement.dto.interview.InterviewResultDto;
import com.smartplacement.dto.interview.ScheduleInterviewRequestDto;
import com.smartplacement.dto.job.EligibilityCriteriaDto;
import com.smartplacement.dto.job.JobCreateRequestDto;
import com.smartplacement.entity.Application;
import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.entity.Company;
import com.smartplacement.entity.InterviewStatus;
import com.smartplacement.entity.InterviewType;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.RoundResult;
import com.smartplacement.entity.SkillProficiency;
import com.smartplacement.entity.Student;
import com.smartplacement.entity.StudentSkill;
import com.smartplacement.entity.User;
import com.smartplacement.entity.UserStatus;
import com.smartplacement.repository.ApplicationRepository;
import com.smartplacement.repository.CompanyRepository;
import com.smartplacement.repository.EligibilityCriteriaRepository;
import com.smartplacement.repository.InterviewRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InterviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.smartplacement.repository.AuditLogRepository auditLogRepository;

    @Autowired
    private com.smartplacement.repository.NotificationRepository notificationRepository;

    @Autowired
    private com.smartplacement.repository.JobOfferRepository jobOfferRepository;

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
        request.setDesignation("Lead Engineering Recruiter");
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
        request.setFirstName("Arjun");
        request.setLastName("Varma");
        request.setBranch("Computer Science and Engineering");
        request.setGraduationYear(2026);
        request.setCgpa(8.9);
        request.setPhone("+919876543333");

        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Student student = studentRepository.findByRollNumber(rollNumber).orElseThrow();
        student.setActiveBacklogs(0);
        student.setTenthPercentage(90.0);
        student.setTwelfthPercentage(88.0);
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
        request.setTitle("Senior Full Stack Developer");
        request.setDescription("Develop high-throughput Java microservices.");
        request.setJobType(JobType.FULL_TIME);
        request.setLocation("Pune");
        request.setSalaryPackageLpa(20.0);
        request.setApplicationDeadline(LocalDateTime.now().plusDays(10));
        request.setDriveDate(LocalDate.now().plusDays(15));
        request.setStatus(JobStatus.PUBLISHED);

        EligibilityCriteriaDto criteria = new EligibilityCriteriaDto();
        criteria.setMinCgpa(7.0);
        criteria.setMaxActiveBacklogs(0);
        criteria.setAllowedBranches(Arrays.asList("Computer Science and Engineering"));
        criteria.setAllowedGradYears(Arrays.asList(2026));
        criteria.setRequiredSkills(Arrays.asList("Java"));
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
    @DisplayName("Recruiter should successfully schedule an interview round for an applicant")
    void recruiterCanScheduleInterview() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@atlassian.com", "Atlassian");
        String studentToken = registerStudentAndGetToken("stud1@univ.edu", "2024CS101");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        ScheduleInterviewRequestDto scheduleReq = new ScheduleInterviewRequestDto();
        scheduleReq.setApplicationId(appId);
        scheduleReq.setRoundNumber(1);
        scheduleReq.setRoundName("Round 1: Data Structures & Algorithms");
        scheduleReq.setInterviewType(InterviewType.ONLINE_MEET);
        scheduleReq.setScheduledAt(LocalDateTime.now().plusDays(3));
        scheduleReq.setMeetingLinkOrVenue("https://meet.google.com/abc-defg-hij");
        scheduleReq.setInterviewerName("Sarah Connor");
        scheduleReq.setInterviewerEmail("sarah@atlassian.com");

        mockMvc.perform(post("/api/v1/interviews/schedule")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.roundNumber", is(1)))
                .andExpect(jsonPath("$.data.roundName", is("Round 1: Data Structures & Algorithms")))
                .andExpect(jsonPath("$.data.interviewerName", is("Sarah Connor")))
                .andExpect(jsonPath("$.data.status", is("SCHEDULED")))
                .andExpect(jsonPath("$.data.meetingLinkOrVenue", containsString("meet.google.com")));

        Application app = applicationRepository.findById(appId).orElseThrow();
        assertEquals(ApplicationStatus.SHORTLISTED, app.getStatus());
    }

    @Test
    @DisplayName("Student can view their own scheduled interview rounds")
    void studentCanViewMyInterviews() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@linkedin.com", "LinkedIn");
        String studentToken = registerStudentAndGetToken("stud2@univ.edu", "2024CS102");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        ScheduleInterviewRequestDto scheduleReq = new ScheduleInterviewRequestDto();
        scheduleReq.setApplicationId(appId);
        scheduleReq.setRoundNumber(1);
        scheduleReq.setRoundName("System Design Discussion");
        scheduleReq.setInterviewType(InterviewType.ONLINE_MEET);
        scheduleReq.setScheduledAt(LocalDateTime.now().plusDays(4));
        scheduleReq.setMeetingLinkOrVenue("https://teams.microsoft.com/l/meetup");
        scheduleReq.setInterviewerName("John Doe");

        mockMvc.perform(post("/api/v1/interviews/schedule")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleReq)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/interviews/my")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].roundName", is("System Design Discussion")))
                .andExpect(jsonPath("$.data.content[0].companyName", is("LinkedIn")));
    }

    @Test
    @DisplayName("Recruiter can record interview result: CLEARED advances candidate stage")
    void recruiterCanSubmitInterviewResultCleared() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@stripe.com", "Stripe");
        String studentToken = registerStudentAndGetToken("stud3@univ.edu", "2024CS103");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        ScheduleInterviewRequestDto scheduleReq = new ScheduleInterviewRequestDto();
        scheduleReq.setApplicationId(appId);
        scheduleReq.setRoundNumber(1);
        scheduleReq.setRoundName("Core Coding Round");
        scheduleReq.setInterviewType(InterviewType.ONLINE_MEET);
        scheduleReq.setScheduledAt(LocalDateTime.now().plusDays(2));
        scheduleReq.setMeetingLinkOrVenue("https://meet.google.com/xyz-uvw");
        scheduleReq.setInterviewerName("Alex Turing");

        MvcResult scheduleRes = mockMvc.perform(post("/api/v1/interviews/schedule")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long interviewId = objectMapper.readTree(scheduleRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        InterviewResultDto resultDto = new InterviewResultDto(
                RoundResult.CLEARED,
                "Strong analytical mindset, solved both algorithmic problems with optimal O(N) complexity.",
                5,
                ApplicationStatus.TECHNICAL_INTERVIEW,
                "Advance to Technical Round 2: System Architecture"
        );

        mockMvc.perform(put("/api/v1/interviews/" + interviewId + "/result")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resultDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result", is("CLEARED")))
                .andExpect(jsonPath("$.data.status", is("COMPLETED")))
                .andExpect(jsonPath("$.data.rating", is(5)));

        Application app = applicationRepository.findById(appId).orElseThrow();
        assertEquals(ApplicationStatus.TECHNICAL_INTERVIEW, app.getStatus());
        assertEquals("Advance to Technical Round 2: System Architecture", app.getCurrentRound());
    }

    @Test
    @DisplayName("Recruiter can record interview result: REJECTED transitions application to terminal state")
    void recruiterCanSubmitInterviewResultRejected() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@palantir.com", "Palantir");
        String studentToken = registerStudentAndGetToken("stud4@univ.edu", "2024CS104");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        ScheduleInterviewRequestDto scheduleReq = new ScheduleInterviewRequestDto();
        scheduleReq.setApplicationId(appId);
        scheduleReq.setRoundNumber(1);
        scheduleReq.setRoundName("Problem Solving Round");
        scheduleReq.setInterviewType(InterviewType.ONLINE_MEET);
        scheduleReq.setScheduledAt(LocalDateTime.now().plusDays(2));
        scheduleReq.setMeetingLinkOrVenue("https://meet.google.com/test");
        scheduleReq.setInterviewerName("Lead Eng");

        MvcResult scheduleRes = mockMvc.perform(post("/api/v1/interviews/schedule")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long interviewId = objectMapper.readTree(scheduleRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        InterviewResultDto resultDto = new InterviewResultDto(
                RoundResult.REJECTED,
                "Unable to demonstrate multi-threading concepts and concurrent programming.",
                2,
                null,
                null
        );

        mockMvc.perform(put("/api/v1/interviews/" + interviewId + "/result")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resultDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.result", is("REJECTED")))
                .andExpect(jsonPath("$.data.status", is("COMPLETED")));

        Application app = applicationRepository.findById(appId).orElseThrow();
        assertEquals(ApplicationStatus.REJECTED, app.getStatus());
        assertEquals("Rejected at Problem Solving Round", app.getCurrentRound());
    }

    @Test
    @DisplayName("Student is forbidden from scheduling interview rounds")
    void studentCannotScheduleInterview() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@vmware.com", "VMware");
        String studentToken = registerStudentAndGetToken("stud5@univ.edu", "2024CS105");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        ScheduleInterviewRequestDto scheduleReq = new ScheduleInterviewRequestDto();
        scheduleReq.setApplicationId(appId);
        scheduleReq.setRoundNumber(1);
        scheduleReq.setRoundName("Hacking");
        scheduleReq.setInterviewType(InterviewType.ONLINE_MEET);
        scheduleReq.setScheduledAt(LocalDateTime.now().plusDays(1));
        scheduleReq.setMeetingLinkOrVenue("link");
        scheduleReq.setInterviewerName("self");

        mockMvc.perform(post("/api/v1/interviews/schedule")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleReq)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Recruiter can cancel an interview round")
    void recruiterCanCancelInterview() throws Exception {
        String recruiterToken = registerRecruiterAndGetToken("recruiter@twosigma.com", "Two Sigma");
        String studentToken = registerStudentAndGetToken("stud6@univ.edu", "2024CS106");
        Long appId = createJobAndApply(recruiterToken, studentToken);

        ScheduleInterviewRequestDto scheduleReq = new ScheduleInterviewRequestDto();
        scheduleReq.setApplicationId(appId);
        scheduleReq.setRoundNumber(1);
        scheduleReq.setRoundName("Quantitative Analysis");
        scheduleReq.setInterviewType(InterviewType.ONLINE_MEET);
        scheduleReq.setScheduledAt(LocalDateTime.now().plusDays(3));
        scheduleReq.setMeetingLinkOrVenue("https://meet.link");
        scheduleReq.setInterviewerName("Quant Panel");

        MvcResult scheduleRes = mockMvc.perform(post("/api/v1/interviews/schedule")
                        .header("Authorization", "Bearer " + recruiterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Long interviewId = objectMapper.readTree(scheduleRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        mockMvc.perform(put("/api/v1/interviews/" + interviewId + "/cancel")
                        .param("reason", "Interviewer unavailable; will reschedule soon")
                        .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/interviews/" + interviewId)
                        .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("CANCELLED")));
    }
}
