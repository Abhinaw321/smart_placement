package com.smartplacement.dto.company;

import jakarta.validation.constraints.NotBlank;

public class CompanyRequestDto {

    @NotBlank(message = "Company name is required")
    private String name;

    private String description;
    private String website;

    @NotBlank(message = "Industry sector is required")
    private String industry;

    private String logoUrl;
    private String address;

    public CompanyRequestDto() {
    }

    public CompanyRequestDto(String name, String description, String website, String industry, String logoUrl, String address) {
        this.name = name;
        this.description = description;
        this.website = website;
        this.industry = industry;
        this.logoUrl = logoUrl;
        this.address = address;
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
}
