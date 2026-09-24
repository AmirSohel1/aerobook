package com.aerobook.notification.controller;

import com.aerobook.notification.dto.BroadcastRequest;
import com.aerobook.notification.dto.NotificationRequest;
import com.aerobook.notification.dto.NotificationResponse;
import com.aerobook.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Notification Operations", description = "Endpoints for managing flight notices, airport gate alerts, and passenger communications")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Notification Service Health Probe", description = "Liveness probe for API Gateway and orchestrators.")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "service", "notification-service",
                "status", "UP",
                "port", 8091,
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    @Operation(summary = "Send targeted notification to user", description = "Creates a persistent notification for a specific user.")
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Broadcast operational alert (Staff & Admin)", description = "Dispatches a platform-wide or flight-specific operational announcement.")
    @PostMapping("/broadcast")
    public ResponseEntity<NotificationResponse> broadcastAlert(
            @Valid @RequestBody BroadcastRequest request,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        NotificationResponse response = notificationService.broadcastAlert(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get user notifications", description = "Retrieves all notifications targeted to a specific passenger ID.")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(
            @Parameter(name = "userId", description = "Passenger user ID", example = "1", required = true)
            @PathVariable Long userId) {

        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @Operation(summary = "Mark notification as read", description = "Sets status to READ and stamps readAt timestamp.")
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @Parameter(name = "id", description = "Notification ID", example = "1", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @Operation(summary = "Mark all notifications as read for a user", description = "Batch updates all unread notifications for a user.")
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            @Parameter(name = "userId", description = "Passenger user ID", example = "1", required = true)
            @PathVariable Long userId) {

        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    @Operation(summary = "Get unread notifications count", description = "Returns the count of unread notifications for notification badges.")
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @Parameter(name = "userId", description = "Passenger user ID", example = "1", required = true)
            @PathVariable Long userId) {

        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("userId", userId, "unreadCount", count));
    }

    @Operation(summary = "List all platform notifications (Admin & Staff)", description = "Returns full notification activity log.")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }
}
