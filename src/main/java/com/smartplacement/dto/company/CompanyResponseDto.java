package com.smartplacement.dto.company;

import com.smartplacement.entity.Company;

import java.time.LocalDateTime;

public class CompanyResponseDto {

    private Long id;
    private String name;
    private String description;
    private String website;
    private String industry;
    private String logoUrl;
    private String address;
    private int recruiterCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CompanyResponseDto() {
    }

    public static CompanyResponseDto fromEntity(Company company) {
        CompanyResponseDto dto = new CompanyResponseDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setDescription(company.getDescription());
        dto.setWebsite(company.getWebsite());
        dto.setIndustry(company.getIndustry());
        dto.setLogoUrl(company.getLogoUrl());
        dto.setAddress(company.getAddress());
        dto.setRecruiterCount(company.getRecruiters() != null ? company.getRecruiters().size() : 0);
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRecruiterCount() {
        return recruiterCount;
    }

    public void setRecruiterCount(int recruiterCount) {
        this.recruiterCount = recruiterCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
