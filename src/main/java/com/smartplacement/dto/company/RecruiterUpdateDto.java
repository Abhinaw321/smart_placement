package com.smartplacement.dto.company;

import jakarta.validation.constraints.NotBlank;

public class RecruiterUpdateDto {

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    public RecruiterUpdateDto() {
    }

    public RecruiterUpdateDto(String designation, String contactPhone) {
        this.designation = designation;
        this.contactPhone = contactPhone;
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
}
