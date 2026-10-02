package com.smartplacement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3 / Swagger Documentation Configuration.
 * Configures global JWT Bearer authentication scheme for interactive Swagger UI testing.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Smart Placement Management System API",
                version = "1.0.0",
                description = "Production-grade RESTful API documentation for Campus Recruitment Automation.",
                contact = @Contact(name = "Smart Placement Team", email = "tpo@university.edu")
        ),
        security = @SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
        name = "BearerAuth",
        description = "Enter JWT Bearer access token received from /api/v1/auth/login",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
