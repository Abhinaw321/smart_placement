package com.smartplacement.repository;

import com.smartplacement.entity.Role;
import com.smartplacement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for User entity.
 *
 * Spring automatically generates SQL implementations at runtime based
 * on method naming conventions (derived query methods).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique login email.
     *
     * @param email user's email address
     * @return Optional containing User if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user already exists with the given email.
     *
     * @param email email to verify
     * @return true if an account exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if any user exists with a given role (useful for bootstrapping admin accounts).
     *
     * @param role user role to check
     * @return true if at least one user with role exists
     */
    boolean existsByRole(Role role);
}
