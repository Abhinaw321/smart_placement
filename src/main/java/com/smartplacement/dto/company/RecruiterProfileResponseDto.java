package com.smartplacement.dto.company;

import com.smartplacement.entity.Recruiter;

import java.time.LocalDateTime;

public class RecruiterProfileResponseDto {

    private Long id;
    private Long userId;
    private String email;
    private String designation;
    private String contactPhone;
    private CompanyResponseDto company;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecruiterProfileResponseDto() {
    }

    public static RecruiterProfileResponseDto fromEntity(Recruiter recruiter) {
        RecruiterProfileResponseDto dto = new RecruiterProfileResponseDto();
        dto.setId(recruiter.getId());
        dto.setUserId(recruiter.getUser().getId());
        dto.setEmail(recruiter.getUser().getEmail());
        dto.setDesignation(recruiter.getDesignation());
        dto.setContactPhone(recruiter.getContactPhone());
        if (recruiter.getCompany() != null) {
            dto.setCompany(CompanyResponseDto.fromEntity(recruiter.getCompany()));
        }
        dto.setCreatedAt(recruiter.getCreatedAt());
        dto.setUpdatedAt(recruiter.getUpdatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public CompanyResponseDto getCompany() {
        return company;
    }

    public void setCompany(CompanyResponseDto company) {
        this.company = company;
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
