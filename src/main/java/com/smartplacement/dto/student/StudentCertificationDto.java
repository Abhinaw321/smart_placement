package com.smartplacement.dto.student;

import com.smartplacement.entity.StudentCertification;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class StudentCertificationDto {

    private Long id;

    @NotBlank(message = "Certification name is required")
    private String name;

    @NotBlank(message = "Issuing organization is required")
    private String issuingOrganization;

    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String credentialId;
    private String credentialUrl;

    public StudentCertificationDto() {
    }

    public StudentCertificationDto(Long id, String name, String issuingOrganization, LocalDate issueDate, LocalDate expiryDate, String credentialId, String credentialUrl) {
        this.id = id;
        this.name = name;
        this.issuingOrganization = issuingOrganization;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.credentialId = credentialId;
        this.credentialUrl = credentialUrl;
    }

    public static StudentCertificationDto fromEntity(StudentCertification cert) {
        return new StudentCertificationDto(
                cert.getId(),
                cert.getName(),
                cert.getIssuingOrganization(),
                cert.getIssueDate(),
                cert.getExpiryDate(),
                cert.getCredentialId(),
                cert.getCredentialUrl()
        );
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

    public String getIssuingOrganization() {
        return issuingOrganization;
    }

    public void setIssuingOrganization(String issuingOrganization) {
        this.issuingOrganization = issuingOrganization;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getCredentialUrl() {
        return credentialUrl;
    }

    public void setCredentialUrl(String credentialUrl) {
        this.credentialUrl = credentialUrl;
    }
}
