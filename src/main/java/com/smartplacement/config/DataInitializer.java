package com.smartplacement.config;

import com.smartplacement.entity.Role;
import com.smartplacement.entity.User;
import com.smartplacement.entity.UserStatus;
import com.smartplacement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes essential default system data upon application startup.
 * Ensures a default TPO Administrator account exists if no admin account is found in the database.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedDefaultAdminIfAbsent();
    }

    private void seedDefaultAdminIfAbsent() {
        String adminEmail = "admin@smartplacement.com";

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(
                    adminEmail,
                    passwordEncoder.encode("Admin@123"),
                    Role.ROLE_TPO_ADMIN,
                    UserStatus.ACTIVE
            );

            userRepository.save(admin);
            log.info("Initialized default TPO Administrator account: {} (Password: Admin@123)", adminEmail);
        } else {
            log.debug("Default TPO Administrator account already present.");
        }
    }
}
