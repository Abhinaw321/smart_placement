package com.smartplacement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartplacement.dto.auth.LoginRequestDto;
import com.smartplacement.dto.auth.RecruiterRegisterRequestDto;
import com.smartplacement.dto.auth.StudentRegisterRequestDto;
import com.smartplacement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.smartplacement.repository.StudentRepository studentRepository;

    @Autowired
    private com.smartplacement.repository.RecruiterRepository recruiterRepository;

    @Autowired
    private com.smartplacement.repository.CompanyRepository companyRepository;

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
    private com.smartplacement.repository.JobRepository jobRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanUp() {
        // Clean up child tables first to satisfy foreign key constraints, then parent tables
        auditLogRepository.deleteAll();
        notificationRepository.deleteAll();
        jobOfferRepository.deleteAll();
        interviewRepository.deleteAll();
        applicationRepository.deleteAll();
        jobRepository.deleteAll();
        studentRepository.deleteAll();
        recruiterRepository.deleteAll();
        companyRepository.deleteAll();
        userRepository.deleteAll();

        // Reseed default admin account
        com.smartplacement.entity.User admin = new com.smartplacement.entity.User(
                "admin@smartplacement.com",
                passwordEncoder.encode("Admin@123"),
                com.smartplacement.entity.Role.ROLE_TPO_ADMIN,
                com.smartplacement.entity.UserStatus.ACTIVE
        );
        userRepository.save(admin);
    }

    @Test
    @DisplayName("Should successfully register a student account and return JWT")
    void testRegisterStudentSuccess() throws Exception {
        StudentRegisterRequestDto request = new StudentRegisterRequestDto();
        request.setEmail("alice@college.edu");
        request.setPassword("Password@123");
        request.setFirstName("Alice");
        request.setLastName("Sharma");
        request.setRollNumber("2024CS01");
        request.setPhone("9876543210");
        request.setBranch("CSE");
        request.setGraduationYear(2028);
        request.setCgpa(8.75);

        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Student account registered successfully"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.email").value("alice@college.edu"))
                .andExpect(jsonPath("$.data.role").value("ROLE_STUDENT"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when registering duplicate email")
    void testRegisterDuplicateEmail() throws Exception {
        StudentRegisterRequestDto request = new StudentRegisterRequestDto();
        request.setEmail("bob@college.edu");
        request.setPassword("Password@123");
        request.setFirstName("Bob");
        request.setLastName("Verma");
        request.setRollNumber("2024CS02");
        request.setPhone("9876543211");
        request.setBranch("CSE");
        request.setGraduationYear(2028);
        request.setCgpa(7.5);

        // First registration
        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Duplicate registration attempt
        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message", containsString("already registered")));
    }

    @Test
    @DisplayName("Should return 400 Bad Request with field validation errors when input is invalid")
    void testRegisterValidationFailure() throws Exception {
        StudentRegisterRequestDto request = new StudentRegisterRequestDto();
        request.setEmail("invalid-email-format");
        request.setPassword("123"); // Too short
        // Missing firstName, lastName, rollNumber, phone, branch, graduationYear, cgpa

        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    @DisplayName("Should successfully register a recruiter account and return JWT")
    void testRegisterRecruiterSuccess() throws Exception {
        RecruiterRegisterRequestDto request = new RecruiterRegisterRequestDto();
        request.setEmail("recruiter@google.com");
        request.setPassword("Google@123");
        request.setCompanyName("Google India");
        request.setDesignation("Lead Talent Acquisition");
        request.setPhone("9988776655");

        mockMvc.perform(post("/api/v1/auth/recruiter/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("ROLE_RECRUITER"))
                .andExpect(jsonPath("$.data.email").value("recruiter@google.com"));
    }

    @Test
    @DisplayName("Should login successfully with valid credentials and return JWT")
    void testLoginSuccess() throws Exception {
        // Register user first
        StudentRegisterRequestDto reg = new StudentRegisterRequestDto();
        reg.setEmail("charlie@college.edu");
        reg.setPassword("MySecret@123");
        reg.setFirstName("Charlie");
        reg.setLastName("Patel");
        reg.setRollNumber("2024IT05");
        reg.setPhone("9812345678");
        reg.setBranch("IT");
        reg.setGraduationYear(2028);
        reg.setCgpa(9.10);

        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        // Perform login
        LoginRequestDto loginRequest = new LoginRequestDto("charlie@college.edu", "MySecret@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Authentication successful"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.email").value("charlie@college.edu"))
                .andExpect(jsonPath("$.data.role").value("ROLE_STUDENT"));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when password is incorrect")
    void testLoginBadCredentials() throws Exception {
        StudentRegisterRequestDto reg = new StudentRegisterRequestDto();
        reg.setEmail("dave@college.edu");
        reg.setPassword("Correct@123");
        reg.setFirstName("Dave");
        reg.setLastName("Kumar");
        reg.setRollNumber("2024ME01");
        reg.setPhone("9812345679");
        reg.setBranch("MECH");
        reg.setGraduationYear(2028);
        reg.setCgpa(8.0);

        mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated());

        // Attempt login with wrong password
        LoginRequestDto loginRequest = new LoginRequestDto("dave@college.edu", "WrongPassword!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Should access /api/v1/auth/me when authenticated with Bearer token")
    void testGetMeWithToken() throws Exception {
        StudentRegisterRequestDto reg = new StudentRegisterRequestDto();
        reg.setEmail("eve@college.edu");
        reg.setPassword("Secret@123");
        reg.setFirstName("Eve");
        reg.setLastName("Roy");
        reg.setRollNumber("2024EC01");
        reg.setPhone("9812345680");
        reg.setBranch("ECE");
        reg.setGraduationYear(2028);
        reg.setCgpa(8.5);

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // Access /api/v1/auth/me with Bearer token
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("eve@college.edu"))
                .andExpect(jsonPath("$.data.role").value("ROLE_STUDENT"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when accessing /api/v1/auth/me without token")
    void testGetMeWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("Should enforce RBAC: allow matching role and forbid non-permitted role")
    void testRoleBasedAccessControl() throws Exception {
        // Register Student
        StudentRegisterRequestDto studentReg = new StudentRegisterRequestDto();
        studentReg.setEmail("student_rbac@college.edu");
        studentReg.setPassword("Secret@123");
        studentReg.setFirstName("Student");
        studentReg.setLastName("User");
        studentReg.setRollNumber("2024CS99");
        studentReg.setPhone("9812345681");
        studentReg.setBranch("CSE");
        studentReg.setGraduationYear(2028);
        studentReg.setCgpa(8.5);

        MvcResult studentResult = mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentReg)))
                .andExpect(status().isCreated())
                .andReturn();

        String studentToken = objectMapper.readTree(studentResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // Student accessing student-only endpoint -> 200 OK
        mockMvc.perform(get("/api/v1/test-rbac/student")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Welcome Student"));

        // Student accessing recruiter-only endpoint -> 403 Forbidden
        mockMvc.perform(get("/api/v1/test-rbac/recruiter")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));

        // Student accessing TPO Admin endpoint -> 403 Forbidden
        mockMvc.perform(get("/api/v1/test-rbac/tpo")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }
}
