package com.smartplacement.dto.offer;

import com.smartplacement.entity.OfferStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for a candidate responding to an employment offer.
 */
public class OfferResponseRequestDto {

    @NotNull(message = "Offer response decision is required (ACCEPTED or DECLINED)")
    private OfferStatus decision;

    @Size(max = 1000, message = "Remarks cannot exceed 1000 characters")
    private String remarks;

    public OfferResponseRequestDto() {
    }

    public OfferResponseRequestDto(OfferStatus decision, String remarks) {
        this.decision = decision;
        this.remarks = remarks;
    }

    public OfferStatus getDecision() {
        return decision;
    }

    public void setDecision(OfferStatus decision) {
        this.decision = decision;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
