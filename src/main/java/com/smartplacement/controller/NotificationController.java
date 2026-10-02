package com.smartplacement.controller;

import com.smartplacement.dto.common.ApiResponse;
import com.smartplacement.dto.common.PagedResponse;
import com.smartplacement.dto.notification.NotificationResponseDto;
import com.smartplacement.security.UserPrincipal;
import com.smartplacement.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller managing user in-app notifications, unread counts, and acknowledgement.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Endpoints for retrieving alerts, unread counts, and acknowledging notifications")
@SecurityRequirement(name = "BearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated inbox notifications for the authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<NotificationResponseDto>>> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        PagedResponse<NotificationResponseDto> notifications = notificationService.getMyNotifications(currentUser, page, size);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get the total count of unread notifications for bell badge display")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@AuthenticationPrincipal UserPrincipal currentUser) {
        long count = notificationService.getUnreadCount(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved successfully", count));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark an individual notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        notificationService.markAsRead(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark all notifications as read for the authenticated user")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@AuthenticationPrincipal UserPrincipal currentUser) {
        notificationService.markAllAsRead(currentUser);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
