package com.smartplacement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartplacement.dto.auth.LoginRequestDto;
import com.smartplacement.dto.auth.RecruiterRegisterRequestDto;
import com.smartplacement.dto.auth.StudentRegisterRequestDto;
import com.smartplacement.dto.student.StudentAcademicUpdateDto;
import com.smartplacement.dto.student.StudentCertificationDto;
import com.smartplacement.dto.student.StudentPersonalInfoUpdateDto;
import com.smartplacement.dto.student.StudentProjectDto;
import com.smartplacement.dto.student.StudentSkillDto;
import com.smartplacement.entity.SkillProficiency;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {

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
    private com.smartplacement.repository.RecruiterRepository recruiterRepository;

    @Autowired
    private com.smartplacement.repository.CompanyRepository companyRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private String studentToken;
    private Long studentId;
    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        auditLogRepository.deleteAll();
        notificationRepository.deleteAll();
        jobOfferRepository.deleteAll();
        interviewRepository.deleteAll();
        applicationRepository.deleteAll();
        jobRepository.deleteAll();
        certificationRepository.deleteAll();
        projectRepository.deleteAll();
        skillRepository.deleteAll();
        studentRepository.deleteAll();
        recruiterRepository.deleteAll();
        companyRepository.deleteAll();
        userRepository.deleteAll();

        // Reseed admin account
        com.smartplacement.entity.User admin = new com.smartplacement.entity.User(
                "admin@smartplacement.com",
                passwordEncoder.encode("Admin@123"),
                com.smartplacement.entity.Role.ROLE_TPO_ADMIN,
                com.smartplacement.entity.UserStatus.ACTIVE
        );
        userRepository.save(admin);

        // 1. Register Student
        StudentRegisterRequestDto reg = new StudentRegisterRequestDto();
        reg.setEmail("student1@college.edu");
        reg.setPassword("Password@123");
        reg.setFirstName("Rahul");
        reg.setLastName("Sharma");
        reg.setRollNumber("2024CS01");
        reg.setPhone("9876543210");
        reg.setGender("MALE");
        reg.setBranch("CSE");
        reg.setGraduationYear(2028);
        reg.setCgpa(8.5);

        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isCreated())
                .andReturn();

        this.studentToken = objectMapper.readTree(regResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // Retrieve student record ID
        MvcResult profileResult = mockMvc.perform(get("/api/v1/students/me")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andReturn();

        this.studentId = objectMapper.readTree(profileResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // 2. Login as initialized Admin
        LoginRequestDto adminLogin = new LoginRequestDto("admin@smartplacement.com", "Admin@123");
        MvcResult adminResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        this.adminToken = objectMapper.readTree(adminResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();
    }

    @Test
    @DisplayName("Should retrieve complete profile for authenticated student")
    void testGetMyProfile() throws Exception {
        mockMvc.perform(get("/api/v1/students/me")
                        .header("Authorization", "Bearer " + studentToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("student1@college.edu"))
                .andExpect(jsonPath("$.data.rollNumber").value("2024CS01"))
                .andExpect(jsonPath("$.data.firstName").value("Rahul"))
                .andExpect(jsonPath("$.data.cgpa").value(8.5))
                .andExpect(jsonPath("$.data.hasResume").value(false));
    }

    @Test
    @DisplayName("Should update personal contact details successfully")
    void testUpdatePersonalInfo() throws Exception {
        StudentPersonalInfoUpdateDto updateDto = new StudentPersonalInfoUpdateDto(
                "Rahul",
                "Verma",
                "9998887770",
                "MALE",
                LocalDate.of(2004, 5, 15)
        );

        mockMvc.perform(put("/api/v1/students/me")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.lastName").value("Verma"))
                .andExpect(jsonPath("$.data.phone").value("9998887770"))
                .andExpect(jsonPath("$.data.dob").value("2004-05-15"));
    }

    @Test
    @DisplayName("Should update academic records successfully")
    void testUpdateAcademicInfo() throws Exception {
        StudentAcademicUpdateDto academicDto = new StudentAcademicUpdateDto();
        academicDto.setRollNumber("2024CS01");
        academicDto.setBranch("CSE");
        academicDto.setGraduationYear(2028);
        academicDto.setCgpa(8.95);
        academicDto.setActiveBacklogs(0);
        academicDto.setHistoryBacklogs(0);
        academicDto.setTenthPercentage(92.5);
        academicDto.setTwelfthPercentage(89.0);
        academicDto.setGapYears(0);

        mockMvc.perform(put("/api/v1/students/me/academic")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(academicDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cgpa").value(8.95))
                .andExpect(jsonPath("$.data.tenthPercentage").value(92.5))
                .andExpect(jsonPath("$.data.twelfthPercentage").value(89.0));
    }

    @Test
    @DisplayName("Should add, list, and delete skills on student profile")
    void testManageSkills() throws Exception {
        StudentSkillDto skillDto = new StudentSkillDto(null, "Java", SkillProficiency.ADVANCED);

        // Add skill
        MvcResult addResult = mockMvc.perform(post("/api/v1/students/me/skills")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skillDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.skillName").value("Java"))
                .andExpect(jsonPath("$.data.proficiency").value("ADVANCED"))
                .andReturn();

        long skillId = objectMapper.readTree(addResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // Verify skill appears in profile
        mockMvc.perform(get("/api/v1/students/me")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.skills", hasSize(1)))
                .andExpect(jsonPath("$.data.skills[0].skillName").value("Java"));

        // Delete skill
        mockMvc.perform(delete("/api/v1/students/me/skills/" + skillId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Skill removed successfully"));

        // Verify skill is removed
        mockMvc.perform(get("/api/v1/students/me")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.skills", hasSize(0)));
    }

    @Test
    @DisplayName("Should add, list, and delete portfolio projects")
    void testManageProjects() throws Exception {
        StudentProjectDto projectDto = new StudentProjectDto(
                null,
                "E-Commerce Portal",
                "Full-stack microservices app",
                "Java, Spring Boot, MySQL",
                "https://github.com/rahul/ecommerce",
                "https://ecommerce.demo.com"
        );

        MvcResult addResult = mockMvc.perform(post("/api/v1/students/me/projects")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("E-Commerce Portal"))
                .andReturn();

        long projectId = objectMapper.readTree(addResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // Delete project
        mockMvc.perform(delete("/api/v1/students/me/projects/" + projectId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should add, list, and delete student certifications")
    void testManageCertifications() throws Exception {
        StudentCertificationDto certDto = new StudentCertificationDto(
                null,
                "AWS Certified Solutions Architect",
                "Amazon Web Services",
                LocalDate.of(2025, 1, 10),
                LocalDate.of(2028, 1, 10),
                "AWS-12345",
                "https://aws.amazon.com/verify/12345"
        );

        MvcResult addResult = mockMvc.perform(post("/api/v1/students/me/certifications")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(certDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("AWS Certified Solutions Architect"))
                .andReturn();

        long certId = objectMapper.readTree(addResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        mockMvc.perform(delete("/api/v1/students/me/certifications/" + certId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should upload PDF resume and allow streaming download")
    void testUploadAndDownloadResume() throws Exception {
        byte[] pdfContent = "%PDF-1.4 official resume content for testing".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "rahul_resume.pdf",
                "application/pdf",
                pdfContent
        );

        // Upload resume
        mockMvc.perform(multipart("/api/v1/students/me/resume")
                        .file(file)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.hasResume").value(true))
                .andExpect(jsonPath("$.data.resumeFilename").value("rahul_resume.pdf"))
                .andExpect(jsonPath("$.data.profileCompleted").value(true));

        // Download own resume
        mockMvc.perform(get("/api/v1/students/me/resume")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfContent));

        // TPO Admin downloading student's resume
        mockMvc.perform(get("/api/v1/students/" + studentId + "/resume")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    @DisplayName("Should allow TPO Admin to search students with pagination and filtering")
    void testTpoSearchStudents() throws Exception {
        mockMvc.perform(get("/api/v1/students")
                        .param("branch", "CSE")
                        .param("gradYear", "2028")
                        .param("minCgpa", "8.0")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].rollNumber").value("2024CS01"));
    }

    @Test
    @DisplayName("Should forbid student from calling admin student search endpoint")
    void testStudentForbiddenFromAdminSearch() throws Exception {
        mockMvc.perform(get("/api/v1/students")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("Should forbid student from accessing another student's profile directly")
    void testStudentForbiddenFromOtherStudentProfile() throws Exception {
        // Register second student
        StudentRegisterRequestDto secondStudent = new StudentRegisterRequestDto();
        secondStudent.setEmail("student2@college.edu");
        secondStudent.setPassword("Password@123");
        secondStudent.setFirstName("Pooja");
        secondStudent.setLastName("Iyer");
        secondStudent.setRollNumber("2024CS02");
        secondStudent.setPhone("9876543299");
        secondStudent.setBranch("CSE");
        secondStudent.setGraduationYear(2028);
        secondStudent.setCgpa(9.0);

        MvcResult secondResult = mockMvc.perform(post("/api/v1/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondStudent)))
                .andExpect(status().isCreated())
                .andReturn();

        String secondToken = objectMapper.readTree(secondResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        // Student 2 trying to access Student 1's profile by ID -> 403 Forbidden
        mockMvc.perform(get("/api/v1/students/" + studentId)
                        .header("Authorization", "Bearer " + secondToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }
}
