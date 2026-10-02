package com.smartplacement.dto.audit;

import com.smartplacement.entity.AuditLog;

import java.time.LocalDateTime;

/**
 * Representation of an immutable audit record for administrative compliance reviews.
 */
public class AuditLogResponseDto {

    private Long id;
    private Long actorId;
    private String actorEmail;
    private String actorRole;
    private String action;
    private String entityName;
    private Long entityId;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;

    public AuditLogResponseDto() {
    }

    public static AuditLogResponseDto fromEntity(AuditLog log) {
        AuditLogResponseDto dto = new AuditLogResponseDto();
        dto.setId(log.getId());
        dto.setActorId(log.getActorId());
        dto.setActorEmail(log.getActorEmail());
        dto.setActorRole(log.getActorRole());
        dto.setAction(log.getAction());
        dto.setEntityName(log.getEntityName());
        dto.setEntityId(log.getEntityId());
        dto.setDetails(log.getDetails());
        dto.setIpAddress(log.getIpAddress());
        dto.setTimestamp(log.getTimestamp());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActorId() {
        return actorId;
    }

    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public void setActorEmail(String actorEmail) {
        this.actorEmail = actorEmail;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
