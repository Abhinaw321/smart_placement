package com.smartplacement.entity;

/**
 * Deterministic recruitment lifecycle stages for candidate job applications.
 */
public enum ApplicationStatus {
    APPLIED,
    SHORTLISTED,
    TEST_SCHEDULED,
    TEST_CLEARED,
    TECHNICAL_INTERVIEW,
    HR_INTERVIEW,
    OFFER_EXTENDED,
    OFFER_ACCEPTED,
    OFFER_DECLINED,
    REJECTED,
    WITHDRAWN;

    /**
     * Checks whether this status represents a terminal state in the recruitment lifecycle.
     */
    public boolean isTerminal() {
        return this == OFFER_ACCEPTED || this == OFFER_DECLINED || this == REJECTED || this == WITHDRAWN;
    }
}
