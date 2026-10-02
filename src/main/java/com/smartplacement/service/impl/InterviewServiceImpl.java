package com.smartplacement.service.impl;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.interview.InterviewResponseDto;
import com.smartplacement.dto.interview.InterviewResultDto;
import com.smartplacement.dto.interview.ScheduleInterviewRequestDto;
import com.smartplacement.entity.Application;
import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.entity.Interview;
import com.smartplacement.entity.InterviewStatus;
import com.smartplacement.entity.Job;
import com.smartplacement.entity.Recruiter;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.RoundResult;
import com.smartplacement.entity.Student;
import com.smartplacement.exception.ApiException;
import com.smartplacement.exception.BadRequestException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.repository.ApplicationRepository;
import com.smartplacement.repository.InterviewRepository;
import com.smartplacement.repository.JobRepository;
import com.smartplacement.repository.RecruiterRepository;
import com.smartplacement.repository.StudentRepository;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.InterviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Production implementation of {@link InterviewService}.
 */
@Service
public class InterviewServiceImpl implements InterviewService {

    private static final Logger log = LoggerFactory.getLogger(InterviewServiceImpl.class);

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;

    public InterviewServiceImpl(InterviewRepository interviewRepository,
                                ApplicationRepository applicationRepository,
                                JobRepository jobRepository,
                                StudentRepository studentRepository,
                                RecruiterRepository recruiterRepository) {
        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.studentRepository = studentRepository;
        this.recruiterRepository = recruiterRepository;
    }

    @Override
    @Transactional
    public InterviewResponseDto scheduleInterview(ScheduleInterviewRequestDto request, UserPrincipal currentUser) {
        Application application = applicationRepository.findByIdWithDetails(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + request.getApplicationId()));

        verifyRecruiterOrAdminAccess(application.getJob(), currentUser);

        if (application.getStatus().isTerminal()) {
            throw new BadRequestException("Cannot schedule an interview for an application in terminal state: " + application.getStatus());
        }

        // Automatic pipeline state synchronization
        if (application.getStatus() == ApplicationStatus.APPLIED) {
            application.setStatus(ApplicationStatus.SHORTLISTED);
        }
        application.setCurrentRound(request.getRoundName() + " (Scheduled for " + request.getScheduledAt() + ")");
        applicationRepository.save(application);

        Interview interview = new Interview(
                application,
                request.getRoundNumber(),
                request.getRoundName().trim(),
                request.getInterviewType(),
                request.getScheduledAt(),
                request.getMeetingLinkOrVenue().trim(),
                request.getInterviewerName().trim(),
                request.getInterviewerEmail() != null ? request.getInterviewerEmail().trim() : null
        );

        Interview saved = interviewRepository.save(interview);
        log.info("Interview '{}' [ID: {}] scheduled for student {} by {}",
                saved.getRoundName(), saved.getId(), application.getStudent().getRollNumber(), currentUser.getUsername());

        return InterviewResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public InterviewResponseDto submitInterviewResult(Long interviewId, InterviewResultDto request, UserPrincipal currentUser) {
        Interview interview = interviewRepository.findByIdWithDetails(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview round not found with ID: " + interviewId));

        verifyRecruiterOrAdminAccess(interview.getApplication().getJob(), currentUser);

        interview.setResult(request.getResult());
        interview.setFeedback(request.getFeedback().trim());
        interview.setRating(request.getRating());
        interview.setStatus(InterviewStatus.COMPLETED);
        interview.setConductedAt(LocalDateTime.now());

        Application application = interview.getApplication();

        if (request.getResult() == RoundResult.CLEARED) {
            if (request.getAdvanceApplicationStatus() != null) {
                application.setStatus(request.getAdvanceApplicationStatus());
            } else if (application.getStatus() == ApplicationStatus.SHORTLISTED) {
                application.setStatus(ApplicationStatus.TECHNICAL_INTERVIEW);
            }

            if (request.getNextRoundName() != null && !request.getNextRoundName().trim().isEmpty()) {
                application.setCurrentRound(request.getNextRoundName().trim());
            } else {
                application.setCurrentRound(interview.getRoundName() + " - Cleared");
            }
        } else if (request.getResult() == RoundResult.REJECTED) {
            application.setStatus(ApplicationStatus.REJECTED);
            application.setRejectionReason("Candidate rejected following " + interview.getRoundName() + ": " + request.getFeedback());
            application.setCurrentRound("Rejected at " + interview.getRoundName());
        } else if (request.getResult() == RoundResult.ON_HOLD) {
            application.setCurrentRound(interview.getRoundName() + " - Evaluation Decision On Hold");
        }

        applicationRepository.save(application);
        Interview saved = interviewRepository.save(interview);

        log.info("Interview [ID: {}] evaluated: result={}, student={}",
                interviewId, request.getResult(), application.getStudent().getRollNumber());

        return InterviewResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InterviewResponseDto> getMyInterviews(UserPrincipal currentUser, int page, int size) {
        Student student = studentRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + currentUser.getId()));

        Pageable pageable = PageRequest.of(page, size);
        Page<Interview> interviewPage = interviewRepository.findStudentInterviewsWithDetails(student.getId(), pageable);

        List<InterviewResponseDto> content = interviewPage.getContent().stream()
                .map(InterviewResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                interviewPage.getNumber(),
                interviewPage.getSize(),
                interviewPage.getTotalElements(),
                interviewPage.getTotalPages(),
                interviewPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponseDto> getInterviewsByApplication(Long applicationId, UserPrincipal currentUser) {
        Application application = applicationRepository.findByIdWithDetails(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + applicationId));

        verifyApplicationParticipantAccess(application, currentUser);

        List<Interview> interviews = interviewRepository.findByApplicationIdOrderByRoundNumberAsc(applicationId);
        return interviews.stream()
                .map(InterviewResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InterviewResponseDto> getInterviewsByJob(Long jobId, int page, int size, UserPrincipal currentUser) {
        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        verifyRecruiterOrAdminAccess(job, currentUser);

        Pageable pageable = PageRequest.of(page, size);
        Page<Interview> interviewPage = interviewRepository.findJobInterviewsWithDetails(jobId, pageable);

        List<InterviewResponseDto> content = interviewPage.getContent().stream()
                .map(InterviewResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                interviewPage.getNumber(),
                interviewPage.getSize(),
                interviewPage.getTotalElements(),
                interviewPage.getTotalPages(),
                interviewPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewResponseDto getInterviewById(Long interviewId, UserPrincipal currentUser) {
        Interview interview = interviewRepository.findByIdWithDetails(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview round not found with ID: " + interviewId));

        verifyApplicationParticipantAccess(interview.getApplication(), currentUser);

        return InterviewResponseDto.fromEntity(interview);
    }

    @Override
    @Transactional
    public void cancelInterview(Long interviewId, String reason, UserPrincipal currentUser) {
        Interview interview = interviewRepository.findByIdWithDetails(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview round not found with ID: " + interviewId));

        verifyRecruiterOrAdminAccess(interview.getApplication().getJob(), currentUser);

        interview.setStatus(InterviewStatus.CANCELLED);
        interview.setFeedback("Interview cancelled: " + (reason != null ? reason : "No reason specified"));
        interviewRepository.save(interview);

        log.info("Interview [ID: {}] cancelled by {}", interviewId, currentUser.getUsername());
    }

    private void verifyRecruiterOrAdminAccess(Job job, UserPrincipal currentUser) {
        if (currentUser.getRole() == Role.ROLE_TPO_ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.ROLE_RECRUITER) {
            Recruiter recruiter = recruiterRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for user ID: " + currentUser.getId()));

            if (!job.getCompany().getId().equals(recruiter.getCompany().getId())) {
                throw new ApiException("You are not authorized to manage interviews for " + job.getCompany().getName(), HttpStatus.FORBIDDEN);
            }
            return;
        }

        throw new ApiException("You do not have permission to manage interview schedules", HttpStatus.FORBIDDEN);
    }

    private void verifyApplicationParticipantAccess(Application application, UserPrincipal currentUser) {
        if (currentUser.getRole() == Role.ROLE_TPO_ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.ROLE_STUDENT) {
            if (application.getStudent().getUser().getId().equals(currentUser.getId())) {
                return;
            }
            throw new ApiException("You can only view your own interview schedule", HttpStatus.FORBIDDEN);
        }

        if (currentUser.getRole() == Role.ROLE_RECRUITER) {
            verifyRecruiterOrAdminAccess(application.getJob(), currentUser);
            return;
        }

        throw new ApiException("You do not have permission to view this interview", HttpStatus.FORBIDDEN);
    }
}
