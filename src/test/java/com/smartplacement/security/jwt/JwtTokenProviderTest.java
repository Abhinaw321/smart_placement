package com.smartplacement.security.jwt;

import com.smartplacement.entity.Role;
import com.smartplacement.entity.User;
import com.smartplacement.entity.UserStatus;
import com.smartplacement.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private static final String SECRET = "9a6747f3a73c7dc852d499ba79478f7e175cf0228254b025b07765ecd1bdf961";
    private static final long EXPIRATION_MS = 3600000; // 1 hour

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(SECRET, EXPIRATION_MS);
    }

    @Test
    @DisplayName("Should successfully generate, sign, and parse a valid JWT token")
    void shouldGenerateAndValidateToken() {
        User user = new User("student@univ.edu", "hashed_pass", Role.ROLE_STUDENT, UserStatus.ACTIVE);
        user.setId(42L);

        UserPrincipal principal = UserPrincipal.create(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String token = tokenProvider.generateToken(auth);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(tokenProvider.validateToken(token));

        assertEquals("student@univ.edu", tokenProvider.getEmailFromToken(token));
        assertEquals(42L, tokenProvider.getUserIdFromToken(token));
        assertEquals("ROLE_STUDENT", tokenProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Should generate token directly from User entity")
    void shouldGenerateTokenFromUser() {
        User user = new User("recruiter@techcorp.com", "hashed_pass", Role.ROLE_RECRUITER, UserStatus.ACTIVE);
        user.setId(101L);

        String token = tokenProvider.generateTokenForUser(user);

        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("recruiter@techcorp.com", tokenProvider.getEmailFromToken(token));
        assertEquals(101L, tokenProvider.getUserIdFromToken(token));
        assertEquals("ROLE_RECRUITER", tokenProvider.getRoleFromToken(token));
    }

    @Test
    @DisplayName("Should return false when validating an invalid or malformed token")
    void shouldRejectMalformedToken() {
        assertFalse(tokenProvider.validateToken("invalid.token.string"));
        assertFalse(tokenProvider.validateToken(""));
        assertFalse(tokenProvider.validateToken(null));
    }

    @Test
    @DisplayName("Should return false when token has expired")
    void shouldRejectExpiredToken() {
        // Expired token provider with negative expiration
        JwtTokenProvider expiredProvider = new JwtTokenProvider(SECRET, -1000);
        User user = new User("expired@univ.edu", "pass", Role.ROLE_STUDENT);
        user.setId(1L);

        String expiredToken = expiredProvider.generateTokenForUser(user);

        assertFalse(tokenProvider.validateToken(expiredToken));
    }
}
