package com.smartplacement.service.impl;

import com.smartplacement.dto.auth.AuthResponseDto;
import com.smartplacement.dto.auth.LoginRequestDto;
import com.smartplacement.dto.auth.RecruiterRegisterRequestDto;
import com.smartplacement.dto.auth.StudentRegisterRequestDto;
import com.smartplacement.dto.auth.UserSummaryDto;
import com.smartplacement.entity.Role;
import com.smartplacement.entity.User;
import com.smartplacement.entity.UserStatus;
import com.smartplacement.exception.BadRequestException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.repository.UserRepository;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.security.jwt.JwtTokenProvider;
import com.smartplacement.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Concrete implementation of AuthService.
 * Handles credential verification, password hashing, and user creation.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        log.info("Attempting login for email: {}", request.getEmail());

        // Authenticate credentials against CustomUserDetailsService & PasswordEncoder
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        // Store authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate cryptographic JWT Bearer token
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        log.info("User logged in successfully: {} with role: {}", userPrincipal.getEmail(), userPrincipal.getRole());

        return new AuthResponseDto(
                jwt,
                tokenProvider.getJwtExpirationMs(),
                userPrincipal.getId(),
                userPrincipal.getEmail(),
                userPrincipal.getRole().name()
        );
    }

    @Override
    @Transactional
    public AuthResponseDto registerStudent(StudentRegisterRequestDto request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        log.info("Registering student with email: {}", normalizedEmail);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("An account is already registered with email: " + normalizedEmail);
        }

        // Create base User account with encoded password and ROLE_STUDENT
        User user = new User(
                normalizedEmail,
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_STUDENT,
                UserStatus.ACTIVE
        );

        User savedUser = userRepository.save(user);
        log.info("Student user created with ID: {}", savedUser.getId());

        String jwt = tokenProvider.generateTokenForUser(savedUser);

        return new AuthResponseDto(
                jwt,
                tokenProvider.getJwtExpirationMs(),
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    @Override
    @Transactional
    public AuthResponseDto registerRecruiter(RecruiterRegisterRequestDto request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        log.info("Registering recruiter with email: {}", normalizedEmail);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("An account is already registered with email: " + normalizedEmail);
        }

        // Create base User account with encoded password and ROLE_RECRUITER
        User user = new User(
                normalizedEmail,
                passwordEncoder.encode(request.getPassword()),
                Role.ROLE_RECRUITER,
                UserStatus.ACTIVE
        );

        User savedUser = userRepository.save(user);
        log.info("Recruiter user created with ID: {}", savedUser.getId());

        String jwt = tokenProvider.generateTokenForUser(savedUser);

        return new AuthResponseDto(
                jwt,
                tokenProvider.getJwtExpirationMs(),
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUser(UserPrincipal principal) {
        if (principal == null) {
            throw new BadRequestException("No authenticated user principal found in current security context");
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + principal.getId()));

        return UserSummaryDto.fromEntity(user);
    }
}
