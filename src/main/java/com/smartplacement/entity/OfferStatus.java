package com.smartplacement.entity;

/**
 * Lifecycle states for formal job and internship offers extended to candidates.
 */
public enum OfferStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    REVOKED;

    public boolean isTerminal() {
        return this == ACCEPTED || this == DECLINED || this == REVOKED;
    }
}
