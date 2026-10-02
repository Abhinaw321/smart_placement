package com.smartplacement.service.impl;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.notification.NotificationResponseDto;
import com.smartplacement.entity.Notification;
import com.smartplacement.entity.NotificationType;
import com.smartplacement.entity.User;
import com.smartplacement.exception.ApiException;
import com.smartplacement.exception.ResourceNotFoundException;
import com.smartplacement.repository.NotificationRepository;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Production implementation of {@link NotificationService}.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public void createNotification(User recipient, String title, String message,
                                   NotificationType type, String referenceType, Long referenceId) {
        if (recipient == null) {
            log.warn("Cannot create notification for null recipient");
            return;
        }

        Notification notification = new Notification(
                recipient,
                title,
                message,
                type,
                referenceType,
                referenceId
        );
        notificationRepository.save(notification);
        log.debug("Notification created for user ID {}: {}", recipient.getId(), title);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<NotificationResponseDto> getMyNotifications(UserPrincipal currentUser, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId(), pageable);

        List<NotificationResponseDto> content = notificationPage.getContent().stream()
                .map(NotificationResponseDto::fromEntity)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalElements(),
                notificationPage.getTotalPages(),
                notificationPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UserPrincipal currentUser) {
        return notificationRepository.countByUserIdAndIsReadFalse(currentUser.getId());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, UserPrincipal currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new ApiException("You are not authorized to view or acknowledge this notification", HttpStatus.FORBIDDEN);
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(UserPrincipal currentUser) {
        notificationRepository.markAllAsReadForUser(currentUser.getId());
    }
}
