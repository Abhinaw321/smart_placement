package com.smartplacement.security;

import com.smartplacement.entity.User;
import com.smartplacement.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service used by Spring Security's AuthenticationManager to load user-specific data.
 *
 * Annotations explained:
 * - @Service: Registers this class as a Spring-managed service bean.
 * - @Transactional(readOnly = true): Keeps the database session open for reading credentials
 *   and ensures optimal query execution without dirty checking.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their unique login email (which acts as the 'username' in our system).
     *
     * @param email login email
     * @return UserDetails representation for Spring Security
     * @throws UsernameNotFoundException if no user is found with this email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return UserPrincipal.create(user);
    }

    /**
     * Loads a user by database primary key ID. Used when resolving authentication from JWT claims.
     *
     * @param id user primary key
     * @return UserDetails representation
     * @throws UsernameNotFoundException if user with ID does not exist
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return UserPrincipal.create(user);
    }
}
