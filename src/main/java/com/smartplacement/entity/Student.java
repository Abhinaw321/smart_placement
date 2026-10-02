package com.smartplacement.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a student's personal, academic, and professional placement profile.
 *
 * Annotations explained:
 * - @OneToOne: Maps strict 1-to-1 link between Student profile and User login credentials.
 * - @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true): Any modification to skills,
 *   projects, or certifications is automatically cascaded and synced to the database.
 */
@Entity
@Table(
        name = "students",
        indexes = {
                @Index(name = "idx_students_user_id", columnList = "user_id", unique = true),
                @Index(name = "idx_students_roll_number", columnList = "roll_number", unique = true),
                @Index(name = "idx_students_branch", columnList = "branch"),
                @Index(name = "idx_students_graduation_year", columnList = "graduation_year"),
                @Index(name = "idx_students_cgpa", columnList = "cgpa")
        }
)
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "roll_number", nullable = false, unique = true, length = 50)
    private String rollNumber;

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "gender", nullable = false, length = 15)
    private String gender = "OTHER";

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "branch", nullable = false, length = 50)
    private String branch;

    @Column(name = "graduation_year", nullable = false)
    private Integer graduationYear;

    @Column(name = "cgpa", nullable = false)
    private Double cgpa = 0.0;

    @Column(name = "active_backlogs", nullable = false)
    private Integer activeBacklogs = 0;

    @Column(name = "history_backlogs", nullable = false)
    private Integer historyBacklogs = 0;

    @Column(name = "tenth_percentage", nullable = false)
    private Double tenthPercentage = 0.0;

    @Column(name = "twelfth_percentage")
    private Double twelfthPercentage;

    @Column(name = "diploma_percentage")
    private Double diplomaPercentage;

    @Column(name = "gap_years", nullable = false)
    private Integer gapYears = 0;

    @Column(name = "resume_url", length = 255)
    private String resumeUrl;

    @Column(name = "resume_filename", length = 150)
    private String resumeFilename;

    @Column(name = "profile_completed", nullable = false)
    private Boolean profileCompleted = false;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentProject> projects = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentCertification> certifications = new ArrayList<>();

    public Student() {
    }

    public Student(User user, String rollNumber, String firstName, String lastName, String phone, String gender, String branch, Integer graduationYear, Double cgpa) {
        this.user = user;
        this.rollNumber = rollNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.gender = gender != null ? gender : "OTHER";
        this.branch = branch;
        this.graduationYear = graduationYear;
        this.cgpa = cgpa != null ? cgpa : 0.0;
        this.profileCompleted = false;
    }

    // Bidirectional helper methods for collections
    public void addSkill(StudentSkill skill) {
        skills.add(skill);
        skill.setStudent(this);
    }

    public void removeSkill(StudentSkill skill) {
        skills.remove(skill);
        skill.setStudent(null);
    }

    public void addProject(StudentProject project) {
        projects.add(project);
        project.setStudent(this);
    }

    public void removeProject(StudentProject project) {
        projects.remove(project);
        project.setStudent(null);
    }

    public void addCertification(StudentCertification certification) {
        certifications.add(certification);
        certification.setStudent(this);
    }

    public void removeCertification(StudentCertification certification) {
        certifications.remove(certification);
        certification.setStudent(null);
    }

    /**
     * Evaluates and updates profile completion percentage status.
     */
    public boolean checkProfileCompletion() {
        boolean hasBasicInfo = firstName != null && lastName != null && phone != null;
        boolean hasAcademicInfo = rollNumber != null && branch != null && graduationYear != null && cgpa != null;
        boolean hasResume = resumeUrl != null && !resumeUrl.isBlank();
        this.profileCompleted = hasBasicInfo && hasAcademicInfo && hasResume;
        return this.profileCompleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Boolean getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(Boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public List<StudentSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<StudentSkill> skills) {
        this.skills = skills;
    }

    public List<StudentProject> getProjects() {
        return projects;
    }

    public void setProjects(List<StudentProject> projects) {
        this.projects = projects;
    }

    public List<StudentCertification> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<StudentCertification> certifications) {
        this.certifications = certifications;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) && Objects.equals(rollNumber, student.rollNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rollNumber);
    }
}
