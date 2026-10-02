package com.smartplacement.service.impl;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.company.CompanyRequestDto;
import com.smartplacement.dto.company.CompanyResponseDto;
import com.smartplacement.entity.Company;
import com.smartplacement.exception.BadRequestException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.repository.CompanyRepository;
import com.smartplacement.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public CompanyResponseDto createCompany(CompanyRequestDto request) {
        String companyName = request.getName().trim();
        if (companyRepository.existsByNameIgnoreCase(companyName)) {
            throw new BadRequestException("A company with this name already exists: " + companyName);
        }

        Company company = new Company(
                companyName,
                request.getDescription(),
                request.getWebsite(),
                request.getIndustry().trim(),
                request.getLogoUrl(),
                request.getAddress()
        );

        Company saved = companyRepository.save(company);
        log.info("Created new company: {} with ID: {}", saved.getName(), saved.getId());
        return CompanyResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public CompanyResponseDto updateCompany(Long id, CompanyRequestDto request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));

        String newName = request.getName().trim();
        if (!company.getName().equalsIgnoreCase(newName) && companyRepository.existsByNameIgnoreCase(newName)) {
            throw new BadRequestException("Company name is already taken by another company: " + newName);
        }

        company.setName(newName);
        company.setDescription(request.getDescription());
        company.setWebsite(request.getWebsite());
        company.setIndustry(request.getIndustry().trim());
        company.setLogoUrl(request.getLogoUrl());
        company.setAddress(request.getAddress());

        Company saved = companyRepository.save(company);
        log.info("Updated company ID: {}", saved.getId());
        return CompanyResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponseDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
        return CompanyResponseDto.fromEntity(company);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CompanyResponseDto> searchCompanies(
            String query, int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Company> companyPage = companyRepository.searchCompanies(query, pageable);

        List<CompanyResponseDto> content = companyPage.getContent().stream()
                .map(CompanyResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                companyPage.getNumber(),
                companyPage.getSize(),
                companyPage.getTotalElements(),
                companyPage.getTotalPages(),
                companyPage.isLast()
        );
    }

    @Override
    @Transactional
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));

        companyRepository.delete(company);
        log.info("Deleted company ID: {}", id);
    }
}
