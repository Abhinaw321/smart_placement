package com.smartplacement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Entity representing in-app notifications and alerts broadcast to platform users.
 */
@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(name = "idx_notif_user_unread", columnList = "user_id, is_read, created_at"),
                @Index(name = "idx_notif_created_at", columnList = "created_at")
        }
)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Lob
    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40)
    private NotificationType type = NotificationType.GENERAL_ALERT;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "reference_type", length = 40)
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    public Notification() {
    }

    public Notification(User user, String title, String message, NotificationType type, String referenceType, Long referenceId) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type != null ? type : NotificationType.GENERAL_ALERT;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.isRead = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        this.isRead = isRead != null ? isRead : false;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
