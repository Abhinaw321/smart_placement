package com.smartplacement.service;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.company.CompanyRequestDto;
import com.smartplacement.dto.company.CompanyResponseDto;

public interface CompanyService {

    CompanyResponseDto createCompany(CompanyRequestDto request);

    CompanyResponseDto updateCompany(Long id, CompanyRequestDto request);

    CompanyResponseDto getCompanyById(Long id);

    PagedResponse<CompanyResponseDto> searchCompanies(String query, int page, int size, String sortBy, String sortDir);

    void deleteCompany(Long id);
}
