package com.smartplacement.service.impl;

import com.smartplacement.dto.application.ApplicationResponseDto;
import com.smartplacement.dto.application.ApplicationStatusUpdateDto;
import com.smartplacement.dto.application.BatchApplicationStatusUpdateDto;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.engine.eligibility.EligibilityEngine;
import com.smartplacement.engine.eligibility.EligibilityReportDto;
import com.smartplacement.entity.Application;
import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.entity.Job;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.Recruiter;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.Student;
import com.smartplacement.exception.ApiException;
import com.smartplacement.exception.BadRequestException;
import com.smartplacement.exception.IneligibleApplicationException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.repository.ApplicationRepository;
import com.smartplacement.repository.JobRepository;
import com.smartplacement.repository.RecruiterRepository;
import com.smartplacement.repository.StudentRepository;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.ApplicationWorkflowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Production implementation of {@link ApplicationWorkflowService}.
 */
@Service
public class ApplicationWorkflowServiceImpl implements ApplicationWorkflowService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationWorkflowServiceImpl.class);

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final EligibilityEngine eligibilityEngine;

    public ApplicationWorkflowServiceImpl(ApplicationRepository applicationRepository,
                                         JobRepository jobRepository,
                                         StudentRepository studentRepository,
                                         RecruiterRepository recruiterRepository,
                                         EligibilityEngine eligibilityEngine) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.studentRepository = studentRepository;
        this.recruiterRepository = recruiterRepository;
        this.eligibilityEngine = eligibilityEngine;
    }

    @Override
    @Transactional
    public ApplicationResponseDto applyForJob(Long jobId, UserPrincipal currentUser) {
        if (currentUser.getRole() != Role.ROLE_STUDENT) {
            throw new ApiException("Only registered students can apply for job opportunities", HttpStatus.FORBIDDEN);
        }

        Student student = studentRepository.findByUserIdWithSkills(currentUser.getId())
                .orElseGet(() -> studentRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + currentUser.getId())));

        if (student.getResumeUrl() == null || student.getResumeUrl().trim().isEmpty()) {
            throw new BadRequestException("You must upload a resume to your profile before applying for campus drives.");
        }

        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job opening not found with ID: " + jobId));

        if (job.getStatus() != JobStatus.PUBLISHED) {
            throw new BadRequestException("This job posting is currently " + job.getStatus() + " and is not accepting applications.");
        }

        if (job.getApplicationDeadline().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("The application deadline for this job posting has expired (" + job.getApplicationDeadline() + ").");
        }

        if (applicationRepository.existsByJobIdAndStudentId(job.getId(), student.getId())) {
            throw new BadRequestException("You have already submitted an application for this job posting.");
        }

        EligibilityReportDto report = eligibilityEngine.evaluate(student, job.getEligibilityCriteria());
        if (!report.isEligible()) {
            String errorMsg = "You do not meet the mandatory eligibility criteria for this job posting: "
                    + String.join("; ", report.getRejectionReasons());
            throw new IneligibleApplicationException(errorMsg, report.getRejectionReasons());
        }

        Application application = new Application(job, student, student.getResumeUrl());
        Application saved = applicationRepository.save(application);

        log.info("Student '{}' [ID: {}] successfully applied for Job '{}' [ID: {}] at {}",
                student.getRollNumber(), student.getId(), job.getTitle(), job.getId(), job.getCompany().getName());

        return ApplicationResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ApplicationResponseDto> getMyApplications(UserPrincipal currentUser, int page, int size, ApplicationStatus status) {
        Student student = studentRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + currentUser.getId()));

        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> appPage = applicationRepository.findStudentApplicationsWithDetails(student.getId(), status, pageable);

        List<ApplicationResponseDto> content = appPage.getContent().stream()
                .map(ApplicationResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                appPage.getNumber(),
                appPage.getSize(),
                appPage.getTotalElements(),
                appPage.getTotalPages(),
                appPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ApplicationResponseDto> getJobApplications(Long jobId, ApplicationStatus status, int page, int size, UserPrincipal currentUser) {
        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        verifyRecruiterOrAdminAccess(job, currentUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<Application> appPage = applicationRepository.findJobApplicationsWithDetails(jobId, status, pageable);

        List<ApplicationResponseDto> content = appPage.getContent().stream()
                .map(ApplicationResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                appPage.getNumber(),
                appPage.getSize(),
                appPage.getTotalElements(),
                appPage.getTotalPages(),
                appPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponseDto getApplicationById(Long applicationId, UserPrincipal currentUser) {
        Application application = applicationRepository.findByIdWithDetails(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        verifyApplicationParticipantAccess(application, currentUser);

        return ApplicationResponseDto.fromEntity(application);
    }

    @Override
    @Transactional
    public ApplicationResponseDto updateApplicationStatus(Long applicationId, ApplicationStatusUpdateDto request, UserPrincipal currentUser) {
        Application application = applicationRepository.findByIdWithDetails(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        verifyRecruiterOrAdminAccess(application.getJob(), currentUser);

        if (application.getStatus().isTerminal() && !request.getStatus().isTerminal()) {
            throw new BadRequestException("Cannot transition application from terminal state "
                    + application.getStatus() + " to active state " + request.getStatus());
        }

        application.setStatus(request.getStatus());

        if (request.getCurrentRound() != null && !request.getCurrentRound().trim().isEmpty()) {
            application.setCurrentRound(request.getCurrentRound().trim());
        }

        if (request.getRejectionReason() != null) {
            application.setRejectionReason(request.getRejectionReason());
        }

        Application updated = applicationRepository.save(application);
        log.info("Application [ID: {}] transitioned to status {} for student {}",
                applicationId, request.getStatus(), updated.getStudent().getRollNumber());

        return ApplicationResponseDto.fromEntity(updated);
    }

    @Override
    @Transactional
    public List<ApplicationResponseDto> batchUpdateApplicationStatus(BatchApplicationStatusUpdateDto request, UserPrincipal currentUser) {
        List<ApplicationResponseDto> updatedList = new ArrayList<>();

        for (Long appId : request.getApplicationIds()) {
            Application application = applicationRepository.findByIdWithDetails(appId)
                    .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + appId));

            verifyRecruiterOrAdminAccess(application.getJob(), currentUser);

            if (application.getStatus().isTerminal() && !request.getStatus().isTerminal()) {
                throw new BadRequestException("Application ID " + appId + " is already in terminal state "
                        + application.getStatus() + " and cannot be transitioned to " + request.getStatus());
            }

            application.setStatus(request.getStatus());

            if (request.getCurrentRound() != null && !request.getCurrentRound().trim().isEmpty()) {
                application.setCurrentRound(request.getCurrentRound().trim());
            }

            if (request.getRejectionReason() != null) {
                application.setRejectionReason(request.getRejectionReason());
            }

            Application saved = applicationRepository.save(application);
            updatedList.add(ApplicationResponseDto.fromEntity(saved));
        }

        log.info("Batch transitioned {} applications to status {} by {}",
                updatedList.size(), request.getStatus(), currentUser.getUsername());

        return updatedList;
    }

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId, UserPrincipal currentUser) {
        Application application = applicationRepository.findByIdWithDetails(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        if (!application.getStudent().getUser().getId().equals(currentUser.getId())) {
            throw new ApiException("You are not authorized to withdraw this application", HttpStatus.FORBIDDEN);
        }

        if (application.getStatus() != ApplicationStatus.APPLIED) {
            throw new BadRequestException("Application cannot be withdrawn once it has progressed beyond APPLIED stage (current status: "
                    + application.getStatus() + ")");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        application.setCurrentRound("Withdrawn by Candidate");
        applicationRepository.save(application);

        log.info("Application [ID: {}] withdrawn by student {}", applicationId, currentUser.getUsername());
    }

    private void verifyRecruiterOrAdminAccess(Job job, UserPrincipal currentUser) {
        if (currentUser.getRole() == Role.ROLE_TPO_ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.ROLE_RECRUITER) {
            Recruiter recruiter = recruiterRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for user ID: " + currentUser.getId()));

            if (!job.getCompany().getId().equals(recruiter.getCompany().getId())) {
                throw new ApiException("You are not authorized to manage candidate applications for "
                        + job.getCompany().getName(), HttpStatus.FORBIDDEN);
            }
            return;
        }

        throw new ApiException("You do not have permission to access applicant pipelines", HttpStatus.FORBIDDEN);
    }

    private void verifyApplicationParticipantAccess(Application application, UserPrincipal currentUser) {
        if (currentUser.getRole() == Role.ROLE_TPO_ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.ROLE_STUDENT) {
            if (application.getStudent().getUser().getId().equals(currentUser.getId())) {
                return;
            }
            throw new ApiException("You can only view your own applications", HttpStatus.FORBIDDEN);
        }

        if (currentUser.getRole() == Role.ROLE_RECRUITER) {
            verifyRecruiterOrAdminAccess(application.getJob(), currentUser);
            return;
        }

        throw new ApiException("You do not have permission to view this application", HttpStatus.FORBIDDEN);
    }
}
