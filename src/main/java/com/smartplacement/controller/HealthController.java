package com.smartplacement.controller;

import com.smartplacement.dto.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller to verify application and database readiness.
 * Accessible at GET /api/v1/health
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private final DataSource dataSource;

    @Autowired
    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealthStatus() {
        Map<String, Object> statusMap = new HashMap<>();
        statusMap.put("status", "UP");
        statusMap.put("service", "smart-placement-system");
        statusMap.put("timestamp", LocalDateTime.now());

        // Test database connectivity
        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(2);
            statusMap.put("database", valid ? "CONNECTED" : "UNREACHABLE");
            statusMap.put("databaseProduct", connection.getMetaData().getDatabaseProductName());
            statusMap.put("databaseVersion", connection.getMetaData().getDatabaseProductVersion());
        } catch (Exception ex) {
            statusMap.put("database", "DISCONNECTED: " + ex.getMessage());
        }

        return ResponseEntity.ok(ApiResponse.success("System health verified", statusMap));
    }
}
