package com.smartplacement.service;

import com.smartplacement.dto.auth.AuthResponseDto;
import com.smartplacement.dto.auth.LoginRequestDto;
import com.smartplacement.dto.auth.RecruiterRegisterRequestDto;
import com.smartplacement.dto.auth.StudentRegisterRequestDto;
import com.smartplacement.dto.auth.UserSummaryDto;
import com.smartplacement.security.UserPrincipal;

/**
 * Service contract for user authentication, registration, and credential operations.
 */
public interface AuthService {

    /**
     * Authenticates user credentials and produces a signed JWT Bearer access token.
     *
     * @param request login payload with email and raw password
     * @return AuthResponseDto containing JWT token and user details
     */
    AuthResponseDto login(LoginRequestDto request);

    /**
     * Registers a new student user account and produces an initial authentication token.
     *
     * @param request student registration payload
     * @return AuthResponseDto containing JWT token and student user details
     */
    AuthResponseDto registerStudent(StudentRegisterRequestDto request);

    /**
     * Registers a new corporate recruiter user account.
     *
     * @param request recruiter registration payload
     * @return AuthResponseDto containing JWT token and recruiter user details
     */
    AuthResponseDto registerRecruiter(RecruiterRegisterRequestDto request);

    /**
     * Retrieves account summary for the currently authenticated principal.
     *
     * @param principal authenticated user principal from SecurityContext
     * @return UserSummaryDto containing user profile details
     */
    UserSummaryDto getCurrentUser(UserPrincipal principal);
}
