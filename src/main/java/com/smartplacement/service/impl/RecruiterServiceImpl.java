package com.smartplacement.service.impl;

import com.smartplacement.dto.company.RecruiterProfileResponseDto;
import com.smartplacement.dto.company.RecruiterUpdateDto;
import com.smartplacement.entity.Recruiter;
import com.smartplacement.exception.BadRequestException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.repository.RecruiterRepository;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.RecruiterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecruiterServiceImpl implements RecruiterService {

    private static final Logger log = LoggerFactory.getLogger(RecruiterServiceImpl.class);

    private final RecruiterRepository recruiterRepository;

    public RecruiterServiceImpl(RecruiterRepository recruiterRepository) {
        this.recruiterRepository = recruiterRepository;
    }

    private Recruiter getRecruiterByPrincipal(UserPrincipal principal) {
        if (principal == null) {
            throw new BadRequestException("Unauthenticated user principal in security context");
        }
        return recruiterRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter profile not found for user: " + principal.getEmail()));
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterProfileResponseDto getMyProfile(UserPrincipal principal) {
        Recruiter recruiter = getRecruiterByPrincipal(principal);
        return RecruiterProfileResponseDto.fromEntity(recruiter);
    }

    @Override
    @Transactional
    public RecruiterProfileResponseDto updateMyProfile(UserPrincipal principal, RecruiterUpdateDto request) {
        Recruiter recruiter = getRecruiterByPrincipal(principal);

        recruiter.setDesignation(request.getDesignation().trim());
        recruiter.setContactPhone(request.getContactPhone().trim());

        Recruiter saved = recruiterRepository.save(recruiter);
        log.info("Updated recruiter profile for user ID: {}", principal.getId());
        return RecruiterProfileResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RecruiterProfileResponseDto getRecruiterById(Long id) {
        Recruiter recruiter = recruiterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recruiter not found with ID: " + id));
        return RecruiterProfileResponseDto.fromEntity(recruiter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecruiterProfileResponseDto> getRecruitersByCompany(Long companyId) {
        return recruiterRepository.findByCompanyId(companyId).stream()
                .map(RecruiterProfileResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
