package com.smartplacement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base abstract entity class providing common audit fields (createdAt, updatedAt)
 * for all persistent domain entities in the system.
 *
 * Annotations explained:
 * - @MappedSuperclass: Tells JPA that this class is NOT a database table itself,
 *   but its fields should be inherited as table columns in all subclasses that extend it.
 * - @EntityListeners(AuditingEntityListener.class): Hooks into Spring Data JPA's auditing
 *   engine to automatically populate @CreatedDate and @LastModifiedDate upon entity insert/update.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
