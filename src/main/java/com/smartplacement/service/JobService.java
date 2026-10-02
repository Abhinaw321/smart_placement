package com.smartplacement.service;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.job.JobCreateRequestDto;
import com.smartplacement.dto.job.JobResponseDto;
import com.smartplacement.dto.job.JobUpdateRequestDto;
import com.smartplacement.dto.job.StudentEligibilityCheckResponseDto;
import com.smartplacement.dto.student.StudentProfileResponseDto;
import com.smartplacement.entity.JobStatus;
import com.smartplacement.entity.JobType;
import com.smartplacement.security.UserPrincipal;

import java.util.List;

/**
 * Service contract for job drive postings and candidate eligibility evaluations.
 */
public interface JobService {

    JobResponseDto createJob(JobCreateRequestDto request, UserPrincipal currentUser);

    JobResponseDto updateJob(Long jobId, JobUpdateRequestDto request, UserPrincipal currentUser);

    JobResponseDto getJobById(Long jobId);

    PagedResponse<JobResponseDto> getAllJobs(int page, int size, JobStatus status, JobType jobType,
                                            Long companyId, String search, UserPrincipal currentUser);

    PagedResponse<JobResponseDto> getJobsByCompany(Long companyId, int page, int size);

    void updateJobStatus(Long jobId, JobStatus newStatus, UserPrincipal currentUser);

    StudentEligibilityCheckResponseDto checkStudentEligibility(Long jobId, UserPrincipal currentUser);

    List<StudentProfileResponseDto> getEligibleStudentsForJob(Long jobId, UserPrincipal currentUser);
}
