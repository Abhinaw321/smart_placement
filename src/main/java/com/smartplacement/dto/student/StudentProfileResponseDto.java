package com.smartplacement.dto.student;

import com.smartplacement.entity.Student;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Complete DTO representing a student's profile for client display.
 */
public class StudentProfileResponseDto {

    private Long id;
    private Long userId;
    private String email;
    private String rollNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dob;
    private String branch;
    private Integer graduationYear;
    private Double cgpa;
    private Integer activeBacklogs;
    private Integer historyBacklogs;
    private Double tenthPercentage;
    private Double twelfthPercentage;
    private Double diplomaPercentage;
    private Integer gapYears;
    private String resumeUrl;
    private String resumeFilename;
    private boolean hasResume;
    private Boolean profileCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<StudentSkillDto> skills = new ArrayList<>();
    private List<StudentProjectDto> projects = new ArrayList<>();
    private List<StudentCertificationDto> certifications = new ArrayList<>();

    public StudentProfileResponseDto() {
    }

    public static StudentProfileResponseDto fromEntity(Student student) {
        StudentProfileResponseDto dto = new StudentProfileResponseDto();
        dto.setId(student.getId());
        dto.setUserId(student.getUser().getId());
        dto.setEmail(student.getUser().getEmail());
        dto.setRollNumber(student.getRollNumber());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setFullName(student.getFirstName() + " " + student.getLastName());
        dto.setPhone(student.getPhone());
        dto.setGender(student.getGender());
        dto.setDob(student.getDob());
        dto.setBranch(student.getBranch());
        dto.setGraduationYear(student.getGraduationYear());
        dto.setCgpa(student.getCgpa());
        dto.setActiveBacklogs(student.getActiveBacklogs());
        dto.setHistoryBacklogs(student.getHistoryBacklogs());
        dto.setTenthPercentage(student.getTenthPercentage());
        dto.setTwelfthPercentage(student.getTwelfthPercentage());
        dto.setDiplomaPercentage(student.getDiplomaPercentage());
        dto.setGapYears(student.getGapYears());
        dto.setResumeUrl(student.getResumeUrl());
        dto.setResumeFilename(student.getResumeFilename());
        dto.setHasResume(student.getResumeUrl() != null && !student.getResumeUrl().isBlank());
        dto.setProfileCompleted(student.getProfileCompleted());
        dto.setCreatedAt(student.getCreatedAt());
        dto.setUpdatedAt(student.getUpdatedAt());

        if (student.getSkills() != null) {
            dto.setSkills(student.getSkills().stream()
                    .map(StudentSkillDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        if (student.getProjects() != null) {
            dto.setProjects(student.getProjects().stream()
                    .map(StudentProjectDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        if (student.getCertifications() != null) {
            dto.setCertifications(student.getCertifications().stream()
                    .map(StudentCertificationDto::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getActiveBacklogs() {
        return activeBacklogs;
    }

    public void setActiveBacklogs(Integer activeBacklogs) {
        this.activeBacklogs = activeBacklogs;
    }

    public Integer getHistoryBacklogs() {
        return historyBacklogs;
    }

    public void setHistoryBacklogs(Integer historyBacklogs) {
        this.historyBacklogs = historyBacklogs;
    }

    public Double getTenthPercentage() {
        return tenthPercentage;
    }

    public void setTenthPercentage(Double tenthPercentage) {
        this.tenthPercentage = tenthPercentage;
    }

    public Double getTwelfthPercentage() {
        return twelfthPercentage;
    }

    public void setTwelfthPercentage(Double twelfthPercentage) {
        this.twelfthPercentage = twelfthPercentage;
    }

    public Double getDiplomaPercentage() {
        return diplomaPercentage;
    }

    public void setDiplomaPercentage(Double diplomaPercentage) {
        this.diplomaPercentage = diplomaPercentage;
    }

    public Integer getGapYears() {
        return gapYears;
    }

    public void setGapYears(Integer gapYears) {
        this.gapYears = gapYears;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getResumeFilename() {
        return resumeFilename;
    }

    public void setResumeFilename(String resumeFilename) {
        this.resumeFilename = resumeFilename;
    }

    public boolean isHasResume() {
        return hasResume;
    }

    public void setHasResume(boolean hasResume) {
        this.hasResume = hasResume;
    }

    public Boolean getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(Boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<StudentSkillDto> getSkills() {
        return skills;
    }

    public void setSkills(List<StudentSkillDto> skills) {
        this.skills = skills;
    }

    public List<StudentProjectDto> getProjects() {
        return projects;
    }

    public void setProjects(List<StudentProjectDto> projects) {
        this.projects = projects;
    }

    public List<StudentCertificationDto> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<StudentCertificationDto> certifications) {
        this.certifications = certifications;
    }
}
