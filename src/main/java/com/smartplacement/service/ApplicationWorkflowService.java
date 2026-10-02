package com.smartplacement.service;

import com.smartplacement.dto.application.ApplicationResponseDto;
import com.smartplacement.dto.application.ApplicationStatusUpdateDto;
import com.smartplacement.dto.application.BatchApplicationStatusUpdateDto;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.entity.ApplicationStatus;
import com.smartplacement.security.UserPrincipal;

import java.util.List;

/**
 * Service contract governing the multi-stage recruitment application lifecycle,
 * state machine transitions, eligibility validation, and applicant pipeline queries.
 */
public interface ApplicationWorkflowService {

    ApplicationResponseDto applyForJob(Long jobId, UserPrincipal currentUser);

    PagedResponse<ApplicationResponseDto> getMyApplications(UserPrincipal currentUser, int page, int size, ApplicationStatus status);

    PagedResponse<ApplicationResponseDto> getJobApplications(Long jobId, ApplicationStatus status, int page, int size, UserPrincipal currentUser);

    ApplicationResponseDto getApplicationById(Long applicationId, UserPrincipal currentUser);

    ApplicationResponseDto updateApplicationStatus(Long applicationId, ApplicationStatusUpdateDto request, UserPrincipal currentUser);

    List<ApplicationResponseDto> batchUpdateApplicationStatus(BatchApplicationStatusUpdateDto request, UserPrincipal currentUser);

    void withdrawApplication(Long applicationId, UserPrincipal currentUser);
}
