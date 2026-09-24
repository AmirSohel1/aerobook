package com.aerobook.notification.dto;

import com.aerobook.notification.entity.Notification;
import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private Long userId;
    private String recipientEmail;
    private String title;
    private String message;
    private String type;
    private String status;
    private String flightNumber;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public NotificationResponse() {
    }

    public NotificationResponse(Notification entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.recipientEmail = entity.getRecipientEmail();
        this.title = entity.getTitle();
        this.message = entity.getMessage();
        this.type = entity.getType() != null ? entity.getType().name() : null;
        this.status = entity.getStatus() != null ? entity.getStatus().name() : null;
        this.flightNumber = entity.getFlightNumber();
        this.createdAt = entity.getCreatedAt();
        this.readAt = entity.getReadAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
