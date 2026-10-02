package com.smartplacement.dto.notification;

import com.smartplacement.entity.Notification;
import com.smartplacement.entity.NotificationType;

import java.time.LocalDateTime;

/**
 * DTO representing an in-app notification for user inbox and bell widget.
 */
public class NotificationResponseDto {

    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private Boolean isRead;
    private String referenceType;
    private Long referenceId;
    private LocalDateTime createdAt;

    public NotificationResponseDto() {
    }

    public static NotificationResponseDto fromEntity(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setIsRead(notification.getIsRead());
        dto.setReferenceType(notification.getReferenceType());
        dto.setReferenceId(notification.getReferenceId());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
