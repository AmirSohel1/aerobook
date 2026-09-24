package com.aerobook.notification.service;

import com.aerobook.notification.dto.BroadcastRequest;
import com.aerobook.notification.dto.NotificationRequest;
import com.aerobook.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse sendNotification(NotificationRequest request);

    NotificationResponse broadcastAlert(BroadcastRequest request);

    List<NotificationResponse> getUserNotifications(Long userId);

    NotificationResponse markAsRead(Long id);

    void markAllAsRead(Long userId);

    long getUnreadCount(Long userId);

    List<NotificationResponse> getAllNotifications();
}
