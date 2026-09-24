package com.aerobook.notification.dto;

import com.aerobook.notification.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;

public class NotificationRequest {

    private Long userId;
    private String recipientEmail;

    @NotBlank(message = "Notification title is required")
    private String title;

    @NotBlank(message = "Notification message content is required")
    private String message;

    private NotificationType type;
    private String flightNumber;

    public NotificationRequest() {
    }

    public NotificationRequest(Long userId, String recipientEmail, String title, String message,
            NotificationType type, String flightNumber) {
        this.userId = userId;
        this.recipientEmail = recipientEmail;
        this.title = title;
        this.message = message;
        this.type = type;
        this.flightNumber = flightNumber;
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }
}
