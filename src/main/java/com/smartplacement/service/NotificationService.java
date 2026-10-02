package com.smartplacement.service;

import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.notification.NotificationResponseDto;
import com.smartplacement.entity.NotificationType;
import com.smartplacement.entity.User;
import com.smartplacement.security.UserPrincipal;

/**
 * Service contract managing real-time and persistent in-app notifications.
 */
public interface NotificationService {

    void createNotification(User recipient, String title, String message,
                            NotificationType type, String referenceType, Long referenceId);

    PagedResponse<NotificationResponseDto> getMyNotifications(UserPrincipal currentUser, int page, int size);

    long getUnreadCount(UserPrincipal currentUser);

    void markAsRead(Long notificationId, UserPrincipal currentUser);

    void markAllAsRead(UserPrincipal currentUser);
}
