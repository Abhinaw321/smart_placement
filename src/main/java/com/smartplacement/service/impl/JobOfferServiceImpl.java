package com.smartplacement.service.impl;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.offer.JobOfferResponseDto;
import com.smartplacement.dto.offer.OfferIssueRequestDto;
import com.smartplacement.dto.offer.OfferResponseRequestDto;
import com.smartplacement.entity.Application;
import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.entity.Job;
import com.smartplacement.entity.JobOffer;
import com.smartplacement.entity.NotificationType;
import com.smartplacement.entity.OfferStatus;
import com.smartplacement.entity.Recruiter;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.Student;
import com.smartplacement.exception.ApiException;
import com.smartplacement.exception.BadRequestException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.repository.ApplicationRepository;
import com.smartplacement.repository.JobOfferRepository;
import com.smartplacement.repository.JobRepository;
import com.smartplacement.repository.RecruiterRepository;
import com.smartplacement.repository.StudentRepository;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.AuditLogService;
import com.smartplacement.service.JobOfferService;
import com.smartplacement.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Production implementation of {@link JobOfferService}.
 */
@Service
public class JobOfferServiceImpl implements JobOfferService {

    private static final Logger log = LoggerFactory.getLogger(JobOfferServiceImpl.class);

    private final JobOfferRepository jobOfferRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public JobOfferServiceImpl(JobOfferRepository jobOfferRepository,
                               ApplicationRepository applicationRepository,
                               StudentRepository studentRepository,
                               JobRepository jobRepository,
                               RecruiterRepository recruiterRepository,
                               NotificationService notificationService,
                               AuditLogService auditLogService) {
        this.jobOfferRepository = jobOfferRepository;
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.recruiterRepository = recruiterRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public JobOfferResponseDto issueOffer(OfferIssueRequestDto request, UserPrincipal currentUser) {
        Application application = applicationRepository.findByIdWithDetails(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with ID: " + request.getApplicationId()));

        verifyRecruiterOrAdminAccess(application.getJob(), currentUser);

        if (application.getStatus().isTerminal()) {
            throw new BadRequestException("Cannot extend an offer to an application in terminal state: " + application.getStatus());
        }

        if (jobOfferRepository.existsByApplicationId(request.getApplicationId())) {
            throw new BadRequestException("An official offer has already been issued for this application");
        }

        // Synchronize application status
        application.setStatus(ApplicationStatus.OFFER_EXTENDED);
        application.setCurrentRound("Offer Extended: " + request.getDesignation() + " (" + request.getCtcLpa() + " LPA)");
        applicationRepository.save(application);

        JobOffer offer = new JobOffer(
                application,
                application.getStudent(),
                application.getJob(),
                request.getCtcLpa(),
                request.getDesignation().trim(),
                LocalDate.now(),
                request.getValidUntil(),
                request.getJoiningDate(),
                request.getOfferLetterUrl() != null ? request.getOfferLetterUrl().trim() : null,
                request.getNotes() != null ? request.getNotes().trim() : null
        );

        JobOffer saved = jobOfferRepository.save(offer);

        // Alert student via in-app notification
        notificationService.createNotification(
                application.getStudent().getUser(),
                "Job Offer Received: " + application.getJob().getCompany().getName(),
                "Congratulations! You have received a formal offer for the position of " +
                        request.getDesignation() + " with CTC " + request.getCtcLpa() + " LPA. Please respond before " + request.getValidUntil(),
                NotificationType.OFFER_RECEIVED,
                "OFFER",
                saved.getId()
        );

        // Record audit trail
        auditLogService.logAction(
                currentUser,
                "OFFER_ISSUED",
                "JobOffer",
                saved.getId(),
                "Extended offer of " + request.getCtcLpa() + " LPA for " + request.getDesignation() + " to " + application.getStudent().getRollNumber()
        );

        log.info("Job offer [ID: {}] issued to student {} for job '{}' by {}",
                saved.getId(), application.getStudent().getRollNumber(), application.getJob().getTitle(), currentUser.getUsername());

        return JobOfferResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public JobOfferResponseDto respondToOffer(Long offerId, OfferResponseRequestDto request, UserPrincipal currentUser) {
        JobOffer offer = jobOfferRepository.findByIdWithDetails(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with ID: " + offerId));

        if (!offer.getStudent().getUser().getId().equals(currentUser.getId())) {
            throw new ApiException("You are only authorized to respond to your own job offers", HttpStatus.FORBIDDEN);
        }

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new BadRequestException("Offer has already been processed with status: " + offer.getStatus());
        }

        // Validate expiration
        if (LocalDate.now().isAfter(offer.getValidUntil())) {
            offer.setStatus(OfferStatus.REVOKED);
            Application app = offer.getApplication();
            app.setStatus(ApplicationStatus.REJECTED);
            app.setRejectionReason("Offer validity deadline expired without student acceptance.");
            applicationRepository.save(app);
            jobOfferRepository.save(offer);
            throw new BadRequestException("Offer validity expired on " + offer.getValidUntil() + ". You can no longer accept this offer.");
        }

        if (request.getDecision() != OfferStatus.ACCEPTED && request.getDecision() != OfferStatus.DECLINED) {
            throw new BadRequestException("Invalid decision response. You may only ACCEPT or DECLINE an offer.");
        }

        Application application = offer.getApplication();
        offer.setStatus(request.getDecision());
        offer.setResponseDate(LocalDateTime.now());
        offer.setStudentRemarks(request.getRemarks() != null ? request.getRemarks().trim() : null);

        if (request.getDecision() == OfferStatus.ACCEPTED) {
            application.setStatus(ApplicationStatus.OFFER_ACCEPTED);
            application.setCurrentRound("Offer Accepted by Candidate");

            // Lock candidate placement status
            Student student = offer.getStudent();
            student.setIsPlaced(true);
            studentRepository.save(student);

            // Audit & Notification
            auditLogService.logAction(
                    currentUser,
                    "OFFER_ACCEPTED",
                    "JobOffer",
                    offer.getId(),
                    "Student " + student.getRollNumber() + " accepted offer from " + offer.getJob().getCompany().getName()
            );

            notificationService.createNotification(
                    student.getUser(),
                    "Offer Accepted Confirmation",
                    "You have successfully accepted the offer from " + offer.getJob().getCompany().getName() + ". Congratulations on your campus placement!",
                    NotificationType.OFFER_RESPONSE,
                    "OFFER",
                    offer.getId()
            );
        } else {
            application.setStatus(ApplicationStatus.OFFER_DECLINED);
            application.setCurrentRound("Offer Declined: " + (request.getRemarks() != null ? request.getRemarks() : "Candidate opted out"));

            auditLogService.logAction(
                    currentUser,
                    "OFFER_DECLINED",
                    "JobOffer",
                    offer.getId(),
                    "Student " + offer.getStudent().getRollNumber() + " declined offer from " + offer.getJob().getCompany().getName()
            );
        }

        applicationRepository.save(application);
        JobOffer saved = jobOfferRepository.save(offer);

        log.info("Student {} responded to offer [ID: {}]: {}",
                offer.getStudent().getRollNumber(), offer.getId(), request.getDecision());

        return JobOfferResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public JobOfferResponseDto revokeOffer(Long offerId, String reason, UserPrincipal currentUser) {
        JobOffer offer = jobOfferRepository.findByIdWithDetails(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with ID: " + offerId));

        verifyRecruiterOrAdminAccess(offer.getJob(), currentUser);

        if (offer.getStatus() == OfferStatus.ACCEPTED) {
            throw new BadRequestException("Cannot revoke an offer that has already been accepted by the candidate");
        }

        offer.setStatus(OfferStatus.REVOKED);
        offer.setNotes("Offer revoked: " + (reason != null ? reason.trim() : "No reason provided"));

        Application app = offer.getApplication();
        app.setStatus(ApplicationStatus.REJECTED);
        app.setRejectionReason("Offer revoked by hiring team: " + (reason != null ? reason.trim() : "Administrative decision"));
        app.setCurrentRound("Offer Revoked");
        applicationRepository.save(app);

        JobOffer saved = jobOfferRepository.save(offer);

        notificationService.createNotification(
                offer.getStudent().getUser(),
                "Offer Revoked Notice",
                "The offer from " + offer.getJob().getCompany().getName() + " for role " + offer.getDesignation() + " has been revoked.",
                NotificationType.OFFER_RESPONSE,
                "OFFER",
                saved.getId()
        );

        auditLogService.logAction(
                currentUser,
                "OFFER_REVOKED",
                "JobOffer",
                saved.getId(),
                "Revoked offer for " + offer.getStudent().getRollNumber() + ". Reason: " + reason
        );

        log.info("Job offer [ID: {}] revoked by {}", offerId, currentUser.getUsername());

        return JobOfferResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JobOfferResponseDto> getMyOffers(UserPrincipal currentUser, int page, int size) {
        Student student = studentRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + currentUser.getId()));

        Pageable pageable = PageRequest.of(page, size);
        Page<JobOffer> offerPage = jobOfferRepository.findByStudentIdWithDetails(student.getId(), pageable);

        List<JobOfferResponseDto> content = offerPage.getContent().stream()
                .map(JobOfferResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                offerPage.getNumber(),
                offerPage.getSize(),
                offerPage.getTotalElements(),
                offerPage.getTotalPages(),
                offerPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JobOfferResponseDto> getOffersByJob(Long jobId, int page, int size, UserPrincipal currentUser) {
        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        verifyRecruiterOrAdminAccess(job, currentUser);

        Pageable pageable = PageRequest.of(page, size);
        Page<JobOffer> offerPage = jobOfferRepository.findByJobIdWithDetails(jobId, pageable);

        List<JobOfferResponseDto> content = offerPage.getContent().stream()
                .map(JobOfferResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                offerPage.getNumber(),
                offerPage.getSize(),
                offerPage.getTotalElements(),
                offerPage.getTotalPages(),
                offerPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public JobOfferResponseDto getOfferById(Long offerId, UserPrincipal currentUser) {
        JobOffer offer = jobOfferRepository.findByIdWithDetails(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job offer not found with ID: " + offerId));

        verifyOfferParticipantAccess(offer, currentUser);

        return JobOfferResponseDto.fromEntity(offer);
    }

    private void verifyRecruiterOrAdminAccess(Job job, UserPrincipal currentUser) {
        if (currentUser.getRole() == Role.ROLE_TPO_ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.ROLE_RECRUITER) {
            Recruiter recruiter = recruiterRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for user ID: " + currentUser.getId()));

            if (!job.getCompany().getId().equals(recruiter.getCompany().getId())) {
                throw new ApiException("You are not authorized to manage job offers for " + job.getCompany().getName(), HttpStatus.FORBIDDEN);
            }
            return;
        }

        throw new ApiException("You do not have permission to manage employment offers", HttpStatus.FORBIDDEN);
    }

    private void verifyOfferParticipantAccess(JobOffer offer, UserPrincipal currentUser) {
        if (currentUser.getRole() == Role.ROLE_TPO_ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.ROLE_STUDENT) {
            if (offer.getStudent().getUser().getId().equals(currentUser.getId())) {
                return;
            }
            throw new ApiException("You are only authorized to view your own employment offers", HttpStatus.FORBIDDEN);
        }

        if (currentUser.getRole() == Role.ROLE_RECRUITER) {
            verifyRecruiterOrAdminAccess(offer.getJob(), currentUser);
            return;
        }

        throw new ApiException("You do not have permission to view this offer", HttpStatus.FORBIDDEN);
    }
}
