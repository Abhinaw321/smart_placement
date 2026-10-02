package com.smartplacement.service;

import com.smartplacement.dto.company.RecruiterProfileResponseDto;
import com.smartplacement.dto.company.RecruiterUpdateDto;
import com.smartplacement.security.UserPrincipal;

import java.util.List;

public interface RecruiterService {

    RecruiterProfileResponseDto getMyProfile(UserPrincipal principal);

    RecruiterProfileResponseDto updateMyProfile(UserPrincipal principal, RecruiterUpdateDto request);

    RecruiterProfileResponseDto getRecruiterById(Long id);

    List<RecruiterProfileResponseDto> getRecruitersByCompany(Long companyId);
}
