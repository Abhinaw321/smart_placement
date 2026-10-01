package com.smartplacement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the Smart Placement Management System (SPMS) backend.
 *
 * Annotations explained:
 * - @SpringBootApplication: Meta-annotation combining:
 *   1. @Configuration (marks class as source of bean definitions)
 *   2. @EnableAutoConfiguration (enables Spring Boot's opinionated auto-configuration)
 *   3. @ComponentScan (scans com.smartplacement package and subpackages for @Component, @Service, @Repository, @Controller)
 *
 * - @EnableJpaAuditing: Enables automatic populating of @CreatedDate and @LastModifiedDate on JPA entities.
 */
@SpringBootApplication
@EnableJpaAuditing
public class SmartPlacementApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartPlacementApplication.class, args);
    }
}
