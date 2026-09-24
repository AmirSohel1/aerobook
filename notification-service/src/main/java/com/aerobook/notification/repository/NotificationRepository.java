package com.aerobook.notification.repository;

import com.aerobook.notification.entity.Notification;
import com.aerobook.notification.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);

    long countByUserIdAndStatus(Long userId, NotificationStatus status);

    List<Notification> findByFlightNumberOrderByCreatedAtDesc(String flightNumber);

    List<Notification> findAllByOrderByCreatedAtDesc();
}
