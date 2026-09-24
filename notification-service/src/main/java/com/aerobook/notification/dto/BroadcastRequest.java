package com.aerobook.notification.dto;

import com.aerobook.notification.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;

public class BroadcastRequest {

    @NotBlank(message = "Broadcast alert title is required")
    private String title;

    @NotBlank(message = "Broadcast message content is required")
    private String message;

    private NotificationType type;
    private String flightNumber;

    public BroadcastRequest() {
    }

    public BroadcastRequest(String title, String message, NotificationType type, String flightNumber) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.flightNumber = flightNumber;
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
