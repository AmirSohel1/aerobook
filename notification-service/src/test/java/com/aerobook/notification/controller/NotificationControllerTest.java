package com.aerobook.notification.controller;

import com.aerobook.notification.dto.BroadcastRequest;
import com.aerobook.notification.dto.NotificationRequest;
import com.aerobook.notification.dto.NotificationResponse;
import com.aerobook.notification.entity.Notification;
import com.aerobook.notification.enums.NotificationType;
import com.aerobook.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private Notification testEntity;

    @BeforeEach
    void setUp() {
        testEntity = new Notification(
                1L,
                "asha.khan@example.com",
                "Flight Confirmed",
                "Your booking for flight AI101 is confirmed.",
                NotificationType.BOOKING_CONFIRMED,
                "AI101"
        );
        testEntity.setId(100L);
    }

    @Test
    void health_ReturnsUpStatus() {
        ResponseEntity<Map<String, Object>> response = notificationController.health();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
        assertThat(response.getBody().get("service")).isEqualTo("notification-service");
        assertThat(response.getBody().get("port")).isEqualTo(8091);
    }

    @Test
    void sendNotification_Success_ReturnsCreated() {
        NotificationResponse resp = new NotificationResponse(testEntity);
        when(notificationService.sendNotification(any(NotificationRequest.class))).thenReturn(resp);

        NotificationRequest req = new NotificationRequest(1L, "asha.khan@example.com", "Flight Confirmed", "Message", NotificationType.BOOKING_CONFIRMED, "AI101");
        ResponseEntity<NotificationResponse> response = notificationController.sendNotification(req);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Flight Confirmed");
        verify(notificationService).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void broadcastAlert_Success_ReturnsCreated() {
        NotificationResponse resp = new NotificationResponse(testEntity);
        when(notificationService.broadcastAlert(any(BroadcastRequest.class))).thenReturn(resp);

        BroadcastRequest req = new BroadcastRequest("Weather Alert", "Flight delayed due to fog", NotificationType.FLIGHT_DELAY, "AI101");
        ResponseEntity<NotificationResponse> response = notificationController.broadcastAlert(req, "ROLE_STAFF");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(notificationService).broadcastAlert(any(BroadcastRequest.class));
    }

    @Test
    void getUserNotifications_ReturnsList() {
        when(notificationService.getUserNotifications(1L)).thenReturn(List.of(new NotificationResponse(testEntity)));

        ResponseEntity<List<NotificationResponse>> response = notificationController.getUserNotifications(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void markAsRead_ReturnsUpdatedNotification() {
        NotificationResponse resp = new NotificationResponse(testEntity);
        resp.setStatus("READ");
        when(notificationService.markAsRead(100L)).thenReturn(resp);

        ResponseEntity<NotificationResponse> response = notificationController.markAsRead(100L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("READ");
    }

    @Test
    void getUnreadCount_ReturnsCountMap() {
        when(notificationService.getUnreadCount(1L)).thenReturn(3L);

        ResponseEntity<Map<String, Object>> response = notificationController.getUnreadCount(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("unreadCount")).isEqualTo(3L);
    }
}
