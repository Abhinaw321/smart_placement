package com.smartplacement.dto.auth;

import com.smartplacement.entity.Role;
import com.smartplacement.entity.User;
import com.smartplacement.entity.UserStatus;

/**
 * Summary DTO of authenticated user details.
 */
public class UserSummaryDto {

    private Long id;
    private String email;
    private Role role;
    private UserStatus status;

    public UserSummaryDto() {
    }

    public UserSummaryDto(Long id, String email, Role role, UserStatus status) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public static UserSummaryDto fromEntity(User user) {
        return new UserSummaryDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}
