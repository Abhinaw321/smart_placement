package com.smartplacement.entity;

/**
 * System user roles enforcing Role-Based Access Control (RBAC).
 *
 * Each role maps directly to Spring Security GrantedAuthority conventions
 * with the mandatory "ROLE_" prefix.
 */
public enum Role {
    /**
     * Undergraduate or postgraduate student eligible for placements.
     */
    ROLE_STUDENT,

    /**
     * Corporate recruiter or talent acquisition specialist posting jobs and shortlisting candidates.
     */
    ROLE_RECRUITER,

    /**
     * Training and Placement Officer / University Administrator with full system oversight.
     */
    ROLE_TPO_ADMIN
}
