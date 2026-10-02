package com.smartplacement.service;

import com.smartplacement.dto.analytics.RecruiterDashboardDto;
import com.smartplacement.dto.analytics.StudentDashboardDto;
import com.smartplacement.dto.analytics.TpoDashboardDto;
import com.smartplacement.security.UserPrincipal;

/**
 * Service contract managing multi-role dashboard analytics, statistical aggregations,
 * and tabular CSV report generation.
 */
public interface AnalyticsService {

    StudentDashboardDto getStudentDashboard(UserPrincipal currentUser);

    RecruiterDashboardDto getRecruiterDashboard(UserPrincipal currentUser);

    TpoDashboardDto getTpoDashboard();

    byte[] exportPlacementsCsv();
}
