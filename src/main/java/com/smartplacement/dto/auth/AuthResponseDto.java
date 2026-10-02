package com.smartplacement.dto.auth;

/**
 * Authentication response returned upon successful login or registration.
 * Contains the signed JWT Bearer access token, token metadata, and user summary.
 */
public class AuthResponseDto {

    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresInMs;
    private Long userId;
    private String email;
    private String role;

    public AuthResponseDto() {
    }

    public AuthResponseDto(String accessToken, long expiresInMs, Long userId, String email, String role) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresInMs = expiresInMs;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
