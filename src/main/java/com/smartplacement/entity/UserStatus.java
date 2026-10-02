package com.smartplacement.entity;

/**
 * Account lifecycle status for users.
 */
public enum UserStatus {
    /**
     * Account is active and authorized to perform permitted actions.
     */
    ACTIVE,

    /**
     * Account is deactivated or pending verification.
     */
    INACTIVE,

    /**
     * Account is locked by an administrator (preventing login).
     */
    LOCKED
}
