package com.smartplacement.security.jwt;

import com.smartplacement.entity.User;
import com.smartplacement.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utility component for generating, signing, parsing, and validating JSON Web Tokens (JWTs).
 *
 * Built using modern JJWT 0.12.x APIs:
 * - Uses HMAC-SHA256 (HS256) cryptographic signing with a 256-bit secret key.
 * - Embeds essential user claims (userId, role) inside the payload for fast stateless authorization.
 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final long jwtExpirationMs;
    private final SecretKey key;

    public JwtTokenProvider(
            @Value("${app.jwt.secret:}") String jwtSecret,
            @Value("${app.jwt.expiration-ms:86400000}") long jwtExpirationMs) {
        this(jwtSecret, jwtExpirationMs, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public JwtTokenProvider(
            @Value("${app.jwt.secret:}") String jwtSecret,
            @Value("${app.jwt.expiration-ms:86400000}") long jwtExpirationMs,
            org.springframework.core.env.Environment environment) {
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            throw new IllegalStateException(
                    "CRITICAL SECURITY CONFIGURATION ERROR: 'JWT_SECRET' (app.jwt.secret) is missing or empty! " +
                    "The application cannot start without a cryptographically secure signing key. " +
                    "Please configure the JWT_SECRET environment variable on your hosting platform."
            );
        }

        if (environment != null && java.util.Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            if (jwtSecret.contains("placeholder") || jwtSecret.getBytes(StandardCharsets.UTF_8).length < 32) {
                throw new IllegalStateException(
                        "CRITICAL SECURITY CONFIGURATION ERROR: In 'prod' profile, 'JWT_SECRET' cannot use dev placeholders " +
                        "and must be at least 256 bits (32 bytes). Please supply a secure, randomly generated JWT_SECRET in production."
                );
            }
        }

        this.jwtExpirationMs = jwtExpirationMs;
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a signed JWT access token for an authenticated Spring Security Authentication principal.
     *
     * @param authentication active Spring Security authentication
     * @return cryptographically signed JWT string
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateToken(userPrincipal.getId(), userPrincipal.getEmail(), userPrincipal.getRole().name());
    }

    /**
     * Generates a signed JWT access token directly from a User entity (e.g. upon user registration).
     *
     * @param user persistent User entity
     * @return cryptographically signed JWT string
     */
    public String generateTokenForUser(User user) {
        return generateToken(user.getId(), user.getEmail(), user.getRole().name());
    }

    /**
     * Internal helper to build the JWT token claims and signature.
     */
    public String generateToken(Long userId, String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts all claims from a signed JWT token after cryptographic verification.
     *
     * @param token signed JWT string
     * @return verified JWT Claims payload
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extracts user email (the 'sub' subject claim) from token.
     *
     * @param token signed JWT string
     * @return user email
     */
    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Extracts numeric user database ID from token claims.
     *
     * @param token signed JWT string
     * @return numeric user ID
     */
    public Long getUserIdFromToken(String token) {
        Object userIdObj = getClaimsFromToken(token).get("userId");
        if (userIdObj instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(userIdObj.toString());
    }

    /**
     * Extracts granted role name from token claims.
     *
     * @param token signed JWT string
     * @return role identifier string (e.g. 'ROLE_STUDENT')
     */
    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

    /**
     * Validates the integrity, cryptographic signature, and expiration of the JWT token.
     *
     * @param authToken raw token string
     * @return true if valid and active, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Malformed JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.warn("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token format: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (JwtException ex) {
            log.error("JWT verification failed: {}", ex.getMessage());
        }
        return false;
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
