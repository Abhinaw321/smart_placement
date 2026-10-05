package com.smartplacement.service.impl;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.job.EligibilityCriteriaDto;
import com.smartplacement.dto.job.JobCreateRequestDto;
import com.smartplacement.dto.job.JobResponseDto;
import com.smartplacement.dto.job.JobUpdateRequestDto;
import com.smartplacement.dto.job.StudentEligibilityCheckResponseDto;
import com.smartplacement.dto.student.StudentProfileResponseDto;
import com.smartplacement.engine.eligibility.EligibilityEngine;
import com.smartplacement.engine.eligibility.EligibilityReportDto;
import com.smartplacement.entity.Company;
import com.smartplacement.entity.EligibilityCriteria;
import com.smartplacement.entity.Job;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import com.smartplacement.entity.Recruiter;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.Student;
import com.smartplacement.exception.ApiException;
import com.smartplacement.exception.BadRequestException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.repository.CompanyRepository;
import com.smartplacement.repository.JobRepository;
import com.smartplacement.repository.RecruiterRepository;
import com.smartplacement.repository.StudentRepository;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Production implementation of {@link JobService}.
 */
@Service
public class JobServiceImpl implements JobService {

    private static final Logger log = LoggerFactory.getLogger(JobServiceImpl.class);

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final RecruiterRepository recruiterRepository;
    private final StudentRepository studentRepository;
    private final EligibilityEngine eligibilityEngine;

    public JobServiceImpl(JobRepository jobRepository,
                          CompanyRepository companyRepository,
                          RecruiterRepository recruiterRepository,
                          StudentRepository studentRepository,
                          EligibilityEngine eligibilityEngine) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.recruiterRepository = recruiterRepository;
        this.studentRepository = studentRepository;
        this.eligibilityEngine = eligibilityEngine;
    }

    @Override
    @Transactional
    public JobResponseDto createJob(JobCreateRequestDto request, UserPrincipal currentUser) {
        Company company;
        Recruiter recruiter = null;

        if (currentUser.getRole() == Role.ROLE_RECRUITER) {
            recruiter = recruiterRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for current user"));
            company = recruiter.getCompany();

            if (!company.isVerified()) {
                throw new BadRequestException("Your company (" + company.getName() + ") has not been verified by the TPO yet. You cannot post jobs until approved.");
            }
        } else if (currentUser.getRole() == Role.ROLE_TPO_ADMIN) {
            if (request.getCompanyId() == null) {
                throw new BadRequestException("Company ID is required when posting a job as TPO Administrator");
            }
            company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));
        } else {
            throw new ApiException("Only recruiters and TPO administrators can create job postings", HttpStatus.FORBIDDEN);
        }

        Job job = new Job(
                company,
                recruiter,
                request.getTitle().trim(),
                request.getDescription(),
                request.getJobType(),
                request.getLocation(),
                request.getSalaryPackageLpa(),
                request.getApplicationDeadline(),
                request.getDriveDate(),
                request.getStatus() != null ? request.getStatus() : JobStatus.PUBLISHED
        );

        if (request.getEligibilityCriteria() != null) {
            EligibilityCriteria criteria = mapToCriteriaEntity(request.getEligibilityCriteria(), job);
            job.setEligibilityCriteria(criteria);
        }

        Job savedJob = jobRepository.save(job);
        log.info("Job drive '{}' [ID: {}] created by {} for company '{}'",
                savedJob.getTitle(), savedJob.getId(), currentUser.getUsername(), company.getName());

        return mapToResponseDto(savedJob);
    }

    @Override
    @Transactional
    public JobResponseDto updateJob(Long jobId, JobUpdateRequestDto request, UserPrincipal currentUser) {
        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        verifyJobModificationAuthority(job, currentUser);

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            job.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            job.setDescription(request.getDescription());
        }
        if (request.getJobType() != null) {
            job.setJobType(request.getJobType());
        }
        if (request.getLocation() != null) {
            job.setLocation(request.getLocation());
        }
        if (request.getSalaryPackageLpa() != null) {
            job.setSalaryPackageLpa(request.getSalaryPackageLpa());
        }
        if (request.getApplicationDeadline() != null) {
            job.setApplicationDeadline(request.getApplicationDeadline());
        }
        if (request.getDriveDate() != null) {
            job.setDriveDate(request.getDriveDate());
        }
        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }

        if (request.getEligibilityCriteria() != null) {
            EligibilityCriteria criteria = job.getEligibilityCriteria();
            if (criteria == null) {
                criteria = mapToCriteriaEntity(request.getEligibilityCriteria(), job);
                job.setEligibilityCriteria(criteria);
            } else {
                updateCriteriaEntity(criteria, request.getEligibilityCriteria());
            }
        }

        Job updatedJob = jobRepository.save(job);
        log.info("Job [ID: {}] updated by user {}", jobId, currentUser.getUsername());
        return mapToResponseDto(updatedJob);
    }

    @Override
    @Transactional(readOnly = true)
    public JobResponseDto getJobById(Long jobId) {
        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));
        return mapToResponseDto(job);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JobResponseDto> getAllJobs(int page, int size, JobStatus status, JobType jobType,
                                                    Long companyId, String search, UserPrincipal currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("applicationDeadline").descending());

        // Default visibility rule: Students can only view PUBLISHED jobs unless explicitly filtering
        JobStatus effectiveStatus = status;
        if (currentUser != null && currentUser.getRole() == Role.ROLE_STUDENT && status == null) {
            effectiveStatus = JobStatus.PUBLISHED;
        }

        Page<Job> jobPage = jobRepository.searchJobs(effectiveStatus, jobType, companyId, search, pageable);
        List<JobResponseDto> content = jobPage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages(),
                jobPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<JobResponseDto> getJobsByCompany(Long companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Job> jobPage = jobRepository.findByCompanyId(companyId, pageable);
        List<JobResponseDto> content = jobPage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                jobPage.getNumber(),
                jobPage.getSize(),
                jobPage.getTotalElements(),
                jobPage.getTotalPages(),
                jobPage.isLast()
        );
    }

    @Override
    @Transactional
    public void updateJobStatus(Long jobId, JobStatus newStatus, UserPrincipal currentUser) {
        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        verifyJobModificationAuthority(job, currentUser);

        job.setStatus(newStatus);
        jobRepository.save(job);
        log.info("Job [ID: {}] status transitioned to {} by {}", jobId, newStatus, currentUser.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentEligibilityCheckResponseDto checkStudentEligibility(Long jobId, UserPrincipal currentUser) {
        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        Student student = studentRepository.findByUserIdWithSkills(currentUser.getId())
                .orElseGet(() -> studentRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user ID: " + currentUser.getId())));

        EligibilityReportDto report = eligibilityEngine.evaluate(student, job.getEligibilityCriteria());

        return new StudentEligibilityCheckResponseDto(
                job.getId(),
                job.getTitle(),
                job.getCompany().getName(),
                student.getId(),
                student.getRollNumber(),
                student.getFirstName() + " " + student.getLastName(),
                report.isEligible(),
                report.getRejectionReasons(),
                report.getCriterionResults()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentProfileResponseDto> getEligibleStudentsForJob(Long jobId, UserPrincipal currentUser) {
        Job job = jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with ID: " + jobId));

        verifyJobModificationAuthority(job, currentUser);

        List<Student> allStudents = studentRepository.findAllWithSkills();
        EligibilityCriteria criteria = job.getEligibilityCriteria();

        return allStudents.stream()
                .filter(student -> eligibilityEngine.isEligible(student, criteria))
                .map(StudentProfileResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void verifyJobModificationAuthority(Job job, UserPrincipal currentUser) {
        if (currentUser.getRole() == Role.ROLE_TPO_ADMIN) {
            return; // TPO has universal modification authority
        }

        if (currentUser.getRole() == Role.ROLE_RECRUITER) {
            Recruiter recruiter = recruiterRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for current user"));

            if (!job.getCompany().getId().equals(recruiter.getCompany().getId())) {
                throw new ApiException("You are not authorized to manage jobs for " + job.getCompany().getName(), HttpStatus.FORBIDDEN);
            }
            return;
        }

        throw new ApiException("You do not have permission to modify job listings", HttpStatus.FORBIDDEN);
    }

    private EligibilityCriteria mapToCriteriaEntity(EligibilityCriteriaDto dto, Job job) {
        EligibilityCriteria criteria = new EligibilityCriteria(
                job,
                dto.getMinCgpa(),
                dto.getMaxActiveBacklogs(),
                dto.getMaxHistoryBacklogs(),
                dto.getMinTenthPercentage(),
                dto.getMinTwelfthPercentage(),
                dto.getMaxGapYears()
        );
        criteria.setAllowedBranches(dto.getAllowedBranches());
        criteria.setAllowedGradYears(dto.getAllowedGradYears());
        criteria.setRequiredSkills(dto.getRequiredSkills());
        return criteria;
    }

    private void updateCriteriaEntity(EligibilityCriteria criteria, EligibilityCriteriaDto dto) {
        if (dto.getMinCgpa() != null) criteria.setMinCgpa(dto.getMinCgpa());
        if (dto.getMaxActiveBacklogs() != null) criteria.setMaxActiveBacklogs(dto.getMaxActiveBacklogs());
        if (dto.getMaxHistoryBacklogs() != null) criteria.setMaxHistoryBacklogs(dto.getMaxHistoryBacklogs());
        if (dto.getMinTenthPercentage() != null) criteria.setMinTenthPercentage(dto.getMinTenthPercentage());
        if (dto.getMinTwelfthPercentage() != null) criteria.setMinTwelfthPercentage(dto.getMinTwelfthPercentage());
        if (dto.getMaxGapYears() != null) criteria.setMaxGapYears(dto.getMaxGapYears());
        if (dto.getAllowedBranches() != null) criteria.setAllowedBranches(dto.getAllowedBranches());
        if (dto.getAllowedGradYears() != null) criteria.setAllowedGradYears(dto.getAllowedGradYears());
        if (dto.getRequiredSkills() != null) criteria.setRequiredSkills(dto.getRequiredSkills());
    }

    private JobResponseDto mapToResponseDto(Job job) {
        JobResponseDto dto = new JobResponseDto();
        dto.setId(job.getId());
        dto.setCompanyId(job.getCompany().getId());
        dto.setCompanyName(job.getCompany().getName());
        dto.setCompanyLogoUrl(job.getCompany().getLogoUrl());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setJobType(job.getJobType());
        dto.setLocation(job.getLocation());
        dto.setSalaryPackageLpa(job.getSalaryPackageLpa());
        dto.setApplicationDeadline(job.getApplicationDeadline());
        dto.setDriveDate(job.getDriveDate());
        dto.setStatus(job.getStatus());
        dto.setCreatedAt(job.getCreatedAt());

        if (job.getCreatedByRecruiter() != null && job.getCreatedByRecruiter().getUser() != null) {
            dto.setCreatedByRecruiterName(job.getCreatedByRecruiter().getDesignation());
        }

        if (job.getEligibilityCriteria() != null) {
            EligibilityCriteria c = job.getEligibilityCriteria();
            dto.setEligibilityCriteria(new EligibilityCriteriaDto(
                    c.getMinCgpa(),
                    c.getMaxActiveBacklogs(),
                    c.getMaxHistoryBacklogs(),
                    c.getMinTenthPercentage(),
                    c.getMinTwelfthPercentage(),
                    c.getMaxGapYears(),
                    c.getAllowedBranches(),
                    c.getAllowedGradYears(),
                    c.getRequiredSkills()
            ));
        }

        return dto;
    }
}
