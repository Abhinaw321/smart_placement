package com.smartplacement.service.impl;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.student.StudentAcademicUpdateDto;
import com.smartplacement.dto.student.StudentCertificationDto;
import com.smartplacement.dto.student.StudentPersonalInfoUpdateDto;
import com.smartplacement.dto.student.StudentProfileResponseDto;
import com.smartplacement.dto.student.StudentProjectDto;
import com.smartplacement.dto.student.StudentSkillDto;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.Student;
import com.smartplacement.entity.StudentCertification;
import com.smartplacement.entity.StudentProject;
import com.smartplacement.entity.StudentSkill;
import com.smartplacement.exception.ApiException;
import com.smartplacement.exception.BadRequestException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.repository.StudentCertificationRepository;
import com.smartplacement.repository.StudentProjectRepository;
import com.smartplacement.repository.StudentRepository;
import com.smartplacement.repository.StudentSkillRepository;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.FileStorageService;
import com.smartplacement.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service implementation managing student profile lifecycle, portfolio items, and resumes.
 */
@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;
    private final StudentSkillRepository skillRepository;
    private final StudentProjectRepository projectRepository;
    private final StudentCertificationRepository certificationRepository;
    private final FileStorageService fileStorageService;

    public StudentServiceImpl(
            StudentRepository studentRepository,
            StudentSkillRepository skillRepository,
            StudentProjectRepository projectRepository,
            StudentCertificationRepository certificationRepository,
            FileStorageService fileStorageService) {
        this.studentRepository = studentRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.certificationRepository = certificationRepository;
        this.fileStorageService = fileStorageService;
    }

    private Student getStudentByPrincipal(UserPrincipal principal) {
        if (principal == null) {
            throw new BadRequestException("Unauthenticated user principal in security context");
        }
        return studentRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + principal.getEmail()));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponseDto getMyProfile(UserPrincipal principal) {
        Student student = getStudentByPrincipal(principal);
        return StudentProfileResponseDto.fromEntity(student);
    }

    @Override
    @Transactional
    public StudentProfileResponseDto updateMyPersonalInfo(UserPrincipal principal, StudentPersonalInfoUpdateDto request) {
        Student student = getStudentByPrincipal(principal);

        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setPhone(request.getPhone().trim());
        if (request.getGender() != null) {
            student.setGender(request.getGender().trim());
        }
        if (request.getDob() != null) {
            student.setDob(request.getDob());
        }

        student.checkProfileCompletion();
        Student saved = studentRepository.save(student);
        log.info("Updated personal info for student ID: {}", saved.getId());
        return StudentProfileResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public StudentProfileResponseDto updateMyAcademicInfo(UserPrincipal principal, StudentAcademicUpdateDto request) {
        Student student = getStudentByPrincipal(principal);

        String newRollNumber = request.getRollNumber().trim();
        if (!student.getRollNumber().equalsIgnoreCase(newRollNumber) && studentRepository.existsByRollNumber(newRollNumber)) {
            throw new BadRequestException("Roll number is already registered by another student: " + newRollNumber);
        }

        student.setRollNumber(newRollNumber);
        student.setBranch(request.getBranch().trim().toUpperCase());
        student.setGraduationYear(request.getGraduationYear());
        student.setCgpa(request.getCgpa());
        student.setActiveBacklogs(request.getActiveBacklogs());
        student.setHistoryBacklogs(request.getHistoryBacklogs());
        student.setTenthPercentage(request.getTenthPercentage());
        student.setTwelfthPercentage(request.getTwelfthPercentage());
        student.setDiplomaPercentage(request.getDiplomaPercentage());
        student.setGapYears(request.getGapYears());

        student.checkProfileCompletion();
        Student saved = studentRepository.save(student);
        log.info("Updated academic info for student ID: {}", saved.getId());
        return StudentProfileResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponseDto getStudentById(Long studentId, UserPrincipal principal) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        // If caller is student, they may only inspect their own profile
        if (principal != null && principal.getRole() == Role.ROLE_STUDENT && !Objects.equals(student.getUser().getId(), principal.getId())) {
            throw new ApiException("Access denied: Students can only view their own profile", HttpStatus.FORBIDDEN);
        }

        return StudentProfileResponseDto.fromEntity(student);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StudentProfileResponseDto> searchStudents(
            String branch, Integer gradYear, Double minCgpa, int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Student> studentPage = studentRepository.searchStudents(branch, gradYear, minCgpa, pageable);

        List<StudentProfileResponseDto> content = studentPage.getContent().stream()
                .map(StudentProfileResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                studentPage.getNumber(),
                studentPage.getSize(),
                studentPage.getTotalElements(),
                studentPage.getTotalPages(),
                studentPage.isLast()
        );
    }

    @Override
    @Transactional
    public StudentSkillDto addSkill(UserPrincipal principal, StudentSkillDto request) {
        Student student = getStudentByPrincipal(principal);

        StudentSkill skill = new StudentSkill(student, request.getSkillName().trim(), request.getProficiency());
        StudentSkill savedSkill = skillRepository.save(skill);
        log.info("Added skill '{}' for student ID: {}", savedSkill.getSkillName(), student.getId());
        return StudentSkillDto.fromEntity(savedSkill);
    }

    @Override
    @Transactional
    public void deleteSkill(UserPrincipal principal, Long skillId) {
        Student student = getStudentByPrincipal(principal);
        StudentSkill skill = skillRepository.findByIdAndStudentId(skillId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillId + " for your profile"));

        skillRepository.delete(skill);
        log.info("Deleted skill ID: {} for student ID: {}", skillId, student.getId());
    }

    @Override
    @Transactional
    public StudentProjectDto addProject(UserPrincipal principal, StudentProjectDto request) {
        Student student = getStudentByPrincipal(principal);

        StudentProject project = new StudentProject(
                student,
                request.getTitle().trim(),
                request.getDescription(),
                request.getTechnologies(),
                request.getGithubUrl(),
                request.getDemoUrl()
        );
        StudentProject saved = projectRepository.save(project);
        log.info("Added project '{}' for student ID: {}", saved.getTitle(), student.getId());
        return StudentProjectDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteProject(UserPrincipal principal, Long projectId) {
        Student student = getStudentByPrincipal(principal);
        StudentProject project = projectRepository.findByIdAndStudentId(projectId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId + " for your profile"));

        projectRepository.delete(project);
        log.info("Deleted project ID: {} for student ID: {}", projectId, student.getId());
    }

    @Override
    @Transactional
    public StudentCertificationDto addCertification(UserPrincipal principal, StudentCertificationDto request) {
        Student student = getStudentByPrincipal(principal);

        StudentCertification cert = new StudentCertification(
                student,
                request.getName().trim(),
                request.getIssuingOrganization().trim(),
                request.getIssueDate(),
                request.getExpiryDate(),
                request.getCredentialId(),
                request.getCredentialUrl()
        );
        StudentCertification saved = certificationRepository.save(cert);
        log.info("Added certification '{}' for student ID: {}", saved.getName(), student.getId());
        return StudentCertificationDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteCertification(UserPrincipal principal, Long certId) {
        Student student = getStudentByPrincipal(principal);
        StudentCertification cert = certificationRepository.findByIdAndStudentId(certId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with ID: " + certId + " for your profile"));

        certificationRepository.delete(cert);
        log.info("Deleted certification ID: {} for student ID: {}", certId, student.getId());
    }

    @Override
    @Transactional
    public StudentProfileResponseDto uploadResume(UserPrincipal principal, MultipartFile file) {
        Student student = getStudentByPrincipal(principal);

        // Delete old resume file if one exists
        if (student.getResumeUrl() != null) {
            fileStorageService.deleteResume(student.getResumeUrl());
        }

        String storedUniqueFilename = fileStorageService.storeResume(file, student.getId());
        student.setResumeUrl(storedUniqueFilename);
        student.setResumeFilename(file.getOriginalFilename());

        student.checkProfileCompletion();
        Student saved = studentRepository.save(student);
        log.info("Uploaded resume '{}' (stored as '{}') for student ID: {}",
                file.getOriginalFilename(), storedUniqueFilename, saved.getId());

        return StudentProfileResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadMyResume(UserPrincipal principal) {
        Student student = getStudentByPrincipal(principal);
        if (student.getResumeUrl() == null || student.getResumeUrl().isBlank()) {
            throw new ResourceNotFoundException("No resume uploaded yet for your profile");
        }
        return fileStorageService.loadResumeAsResource(student.getResumeUrl());
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadStudentResume(Long studentId, UserPrincipal principal) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        if (principal != null && principal.getRole() == Role.ROLE_STUDENT && !Objects.equals(student.getUser().getId(), principal.getId())) {
            throw new ApiException("Access denied: Students can only download their own resume", HttpStatus.FORBIDDEN);
        }

        if (student.getResumeUrl() == null || student.getResumeUrl().isBlank()) {
            throw new ResourceNotFoundException("Student with ID " + studentId + " has not uploaded a resume yet");
        }

        return fileStorageService.loadResumeAsResource(student.getResumeUrl());
    }

    @Override
    @Transactional(readOnly = true)
    public String getResumeFilename(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
        return student.getResumeFilename() != null ? student.getResumeFilename() : "resume.pdf";
    }
}
