package com.smartplacement.service;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.interview.InterviewResponseDto;
import com.smartplacement.dto.interview.InterviewResultDto;
import com.smartplacement.dto.interview.ScheduleInterviewRequestDto;
import com.smartplacement.security.UserPrincipal;

import java.util.List;

/**
 * Service contract managing the scheduling, feedback recording, and lifecycle outcomes
 * for candidate technical assessments and interviews.
 */
public interface InterviewService {

    InterviewResponseDto scheduleInterview(ScheduleInterviewRequestDto request, UserPrincipal currentUser);

    InterviewResponseDto submitInterviewResult(Long interviewId, InterviewResultDto request, UserPrincipal currentUser);

    PagedResponse<InterviewResponseDto> getMyInterviews(UserPrincipal currentUser, int page, int size);

    List<InterviewResponseDto> getInterviewsByApplication(Long applicationId, UserPrincipal currentUser);

    PagedResponse<InterviewResponseDto> getInterviewsByJob(Long jobId, int page, int size, UserPrincipal currentUser);

    InterviewResponseDto getInterviewById(Long interviewId, UserPrincipal currentUser);

    void cancelInterview(Long interviewId, String reason, UserPrincipal currentUser);
}
