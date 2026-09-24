package com.aerobook.notification.service;

import com.aerobook.notification.dto.BroadcastRequest;
import com.aerobook.notification.dto.NotificationRequest;
import com.aerobook.notification.dto.NotificationResponse;
import com.aerobook.notification.entity.Notification;
import com.aerobook.notification.enums.NotificationStatus;
import com.aerobook.notification.enums.NotificationType;
import com.aerobook.notification.repository.NotificationRepository;
import com.aerobook.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification testEntity;

    @BeforeEach
    void setUp() {
        testEntity = new Notification(
                1L,
                "asha.khan@example.com",
                "Flight Gate Changed",
                "Your flight AI101 gate has moved to Gate 4B.",
                NotificationType.GATE_CHANGE,
                "AI101"
        );
        testEntity.setId(10L);
    }

    @Test
    void sendNotification_SavesAndReturnsResponse() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            n.setId(10L);
            return n;
        });

        NotificationRequest req = new NotificationRequest(1L, "asha.khan@example.com", "Flight Gate Changed",
                "Gate 4B", NotificationType.GATE_CHANGE, "AI101");

        NotificationResponse response = notificationService.sendNotification(req);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("Flight Gate Changed");
        assertThat(response.getStatus()).isEqualTo("UNREAD");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void broadcastAlert_SavesWithNullUserId() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            n.setId(20L);
            return n;
        });

        BroadcastRequest req = new BroadcastRequest("Airport Weather Warning", "Dense fog delay",
                NotificationType.FLIGHT_DELAY, "AI101");

        NotificationResponse response = notificationService.broadcastAlert(req);

        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getUserId()).isNull();
        assertThat(response.getType()).isEqualTo("FLIGHT_DELAY");
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void markAsRead_UpdatesStatusToRead() {
        when(notificationRepository.findById(10L)).thenReturn(Optional.of(testEntity));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponse response = notificationService.markAsRead(10L);

        assertThat(response.getStatus()).isEqualTo("READ");
        assertThat(response.getReadAt()).isNotNull();
        verify(notificationRepository).save(testEntity);
    }

    @Test
    void getUnreadCount_ReturnsRepoCount() {
        when(notificationRepository.countByUserIdAndStatus(1L, NotificationStatus.UNREAD)).thenReturn(5L);

        long count = notificationService.getUnreadCount(1L);

        assertThat(count).isEqualTo(5L);
    }
}
