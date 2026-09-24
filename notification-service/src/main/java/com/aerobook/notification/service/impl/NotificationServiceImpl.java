package com.aerobook.notification.service.impl;

import com.aerobook.notification.dto.BroadcastRequest;
import com.aerobook.notification.dto.NotificationRequest;
import com.aerobook.notification.dto.NotificationResponse;
import com.aerobook.notification.entity.Notification;
import com.aerobook.notification.enums.NotificationStatus;
import com.aerobook.notification.enums.NotificationType;
import com.aerobook.notification.repository.NotificationRepository;
import com.aerobook.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        log.info("Sending targeted notification to User ID: {} ({}) with title: '{}'",
                request.getUserId(), request.getRecipientEmail(), request.getTitle());

        Notification entity = new Notification(
                request.getUserId(),
                request.getRecipientEmail(),
                request.getTitle(),
                request.getMessage(),
                request.getType() != null ? request.getType() : NotificationType.INFO,
                request.getFlightNumber()
        );

        Notification saved = notificationRepository.save(entity);
        log.info("Notification successfully saved with ID: {}", saved.getId());
        return new NotificationResponse(saved);
    }

    @Override
    public NotificationResponse broadcastAlert(BroadcastRequest request) {
        log.info("Broadcasting alert with title: '{}', flight: '{}'", request.getTitle(), request.getFlightNumber());

        Notification entity = new Notification(
                null, // null userId means broadcast to all
                null,
                request.getTitle(),
                request.getMessage(),
                request.getType() != null ? request.getType() : NotificationType.BROADCAST,
                request.getFlightNumber()
        );

        Notification saved = notificationRepository.save(entity);
        log.info("Broadcast notification created with ID: {}", saved.getId());
        return new NotificationResponse(saved);
    }

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        log.debug("Fetching notifications for User ID: {}", userId);
        List<Notification> userSpecific = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return userSpecific.stream().map(NotificationResponse::new).toList();
    }

    @Override
    public NotificationResponse markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + id));

        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        Notification saved = notificationRepository.save(notification);
        log.info("Notification ID: {} marked as READ", id);
        return new NotificationResponse(saved);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        LocalDateTime now = LocalDateTime.now();
        for (Notification n : notifications) {
            if (n.getStatus() == NotificationStatus.UNREAD) {
                n.setStatus(NotificationStatus.READ);
                n.setReadAt(now);
            }
        }
        notificationRepository.saveAll(notifications);
        log.info("All unread notifications for User ID: {} marked as READ", userId);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(NotificationResponse::new).toList();
    }
}
