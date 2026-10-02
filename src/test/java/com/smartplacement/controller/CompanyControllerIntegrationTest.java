package com.smartplacement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartplacement.dto.auth.LoginRequestDto;
import com.smartplacement.dto.auth.RecruiterRegisterRequestDto;
import com.smartplacement.dto.auth.StudentRegisterRequestDto;
import com.smartplacement.dto.company.CompanyRequestDto;
import com.smartplacement.dto.company.RecruiterUpdateDto;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.User;
import com.smartplacement.entity.UserStatus;
import com.smartplacement.repository.CompanyRepository;
import com.smartplacement.repository.RecruiterRepository;
import com.smartplacement.repository.StudentRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CompanyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private com.smartplacement.repository.JobRepository jobRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() throws Exception {
        jobRepository.deleteAll();
        studentRepository.deleteAll();
        recruiterRepository.deleteAll();
        companyRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Reseed Admin
        User admin = new User(
                "admin@smartplacement.com",
                passwordEncoder.encode("Admin@123"),
                Role.ROLE_TPO_ADMIN,
                UserStatus.ACTIVE
        );
        userRepository.save(admin);

        LoginRequestDto adminLogin = new LoginRequestDto("admin@smartplacement.com", "Admin@123");
        MvcResult adminResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();
        this.adminToken = objectMapper.readTree(adminResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // 2. Register Student
        StudentRegisterRequestDto studentReg = new StudentRegisterRequestDto();
        studentReg.setEmail("student_comp@univ.edu");
        studentReg.setPassword("Pass@123");
        studentReg.setFirstName("Karan");
        studentReg.setLastName("Johar");
        studentReg.setRollNumber("2024CS55");
        studentReg.setPhone("9988776655");
        studentReg.setBranch("CSE");
        studentReg.setGraduationYear(2028);
        studentReg.setCgpa(8.2);

        MvcResult studentResult = mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentReg)))
                .andExpect(status().isCreated())
                .andReturn();
        this.studentToken = objectMapper.readTree(studentResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();
    }

    @Test
    @DisplayName("Should register recruiter, auto-create company, and retrieve recruiter profile")
    void testRegisterRecruiterAndGetProfile() throws Exception {
        RecruiterRegisterRequestDto reg = new RecruiterRegisterRequestDto();
        reg.setEmail("recruiter@microsoft.com");
        reg.setPassword("Micro@123");
        reg.setCompanyName("Microsoft India");
        reg.setDesignation("Lead Technical Recruiter");
        reg.setPhone("9876543210");

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/recruiter/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("ROLE_RECRUITER"))
                .andReturn();

        String recruiterToken = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // Retrieve recruiter profile
        mockMvc.perform(get("/api/v1/recruiters/me")
                        .header("Authorization", "Bearer " + recruiterToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("recruiter@microsoft.com"))
                .andExpect(jsonPath("$.data.designation").value("Lead Technical Recruiter"))
                .andExpect(jsonPath("$.data.company.name").value("Microsoft India"));
    }

    @Test
    @DisplayName("Should update recruiter designation and contact phone")
    void testUpdateRecruiterProfile() throws Exception {
        RecruiterRegisterRequestDto reg = new RecruiterRegisterRequestDto();
        reg.setEmail("recruiter2@amazon.com");
        reg.setPassword("Amazon@123");
        reg.setCompanyName("Amazon Development Centre");
        reg.setDesignation("Talent Scout");
        reg.setPhone("9876543211");

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/recruiter/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        RecruiterUpdateDto updateDto = new RecruiterUpdateDto("Senior Director of Recruiting", "9112233445");

        mockMvc.perform(put("/api/v1/recruiters/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.designation").value("Senior Director of Recruiting"))
                .andExpect(jsonPath("$.data.contactPhone").value("9112233445"));
    }

    @Test
    @DisplayName("Should create company successfully as TPO Admin")
    void testCreateCompany() throws Exception {
        CompanyRequestDto companyDto = new CompanyRequestDto(
                "Google LLC",
                "Leading search and cloud technology enterprise",
                "https://careers.google.com",
                "Cloud & AI",
                "https://google.com/logo.png",
                "Mountain View, CA / Hyderabad, India"
        );

        mockMvc.perform(post("/api/v1/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Google LLC"))
                .andExpect(jsonPath("$.data.industry").value("Cloud & AI"));
    }

    @Test
    @DisplayName("Should fail when creating duplicate company name")
    void testCreateDuplicateCompany() throws Exception {
        CompanyRequestDto companyDto = new CompanyRequestDto(
                "Apple Inc",
                "Consumer electronics & software",
                "https://apple.com",
                "Hardware & Software",
                null,
                "Cupertino, CA"
        );

        mockMvc.perform(post("/api/v1/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyDto)))
                .andExpect(status().isCreated());

        // Duplicate attempt
        mockMvc.perform(post("/api/v1/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("Should allow authenticated students to browse and search companies")
    void testStudentBrowseCompanies() throws Exception {
        CompanyRequestDto c1 = new CompanyRequestDto("Goldman Sachs", "Investment banking", "https://gs.com", "Fintech", null, "New York");
        CompanyRequestDto c2 = new CompanyRequestDto("Morgan Stanley", "Financial services", "https://ms.com", "Fintech", null, "New York");

        mockMvc.perform(post("/api/v1/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c2)))
                .andExpect(status().isCreated());

        // Student searching for "Goldman"
        mockMvc.perform(get("/api/v1/companies")
                        .param("query", "Goldman")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("Goldman Sachs"));
    }

    @Test
    @DisplayName("Should update existing company details")
    void testUpdateCompany() throws Exception {
        CompanyRequestDto initial = new CompanyRequestDto("Infosys", "IT Services", "https://infosys.com", "Consulting", null, "Bangalore");
        MvcResult result = mockMvc.perform(post("/api/v1/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initial)))
                .andExpect(status().isCreated())
                .andReturn();

        long companyId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        CompanyRequestDto updateDto = new CompanyRequestDto("Infosys Limited", "Global Digital Services & AI", "https://infosys.com/about", "Information Technology", null, "Bangalore");

        mockMvc.perform(put("/api/v1/companies/" + companyId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Infosys Limited"))
                .andExpect(jsonPath("$.data.description").value("Global Digital Services & AI"));
    }

    @Test
    @DisplayName("Should forbid student from deleting a company")
    void testStudentForbiddenFromDeletingCompany() throws Exception {
        CompanyRequestDto c = new CompanyRequestDto("TCS", "IT Services", "https://tcs.com", "IT", null, "Mumbai");
        MvcResult result = mockMvc.perform(post("/api/v1/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isCreated())
                .andReturn();

        long companyId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        mockMvc.perform(delete("/api/v1/companies/" + companyId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Should allow TPO Admin to delete a company")
    void testAdminDeleteCompany() throws Exception {
        CompanyRequestDto c = new CompanyRequestDto("Wipro", "IT Consulting", "https://wipro.com", "IT", null, "Bangalore");
        MvcResult result = mockMvc.perform(post("/api/v1/companies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isCreated())
                .andReturn();

        long companyId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        mockMvc.perform(delete("/api/v1/companies/" + companyId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Company deleted successfully"));

        // Verify company is gone
        mockMvc.perform(get("/api/v1/companies/" + companyId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
