package com.skillchain.model;

import java.time.LocalDateTime;

/**
 * ============================================================================
 * JAVA OOP CONCEPT: CLASSES & ENCAPSULATION
 * ============================================================================
 * Represents a student notification event (e.g. accepted proposal, new incoming request).
 */
public class AppNotification {

    public enum NotificationType {
        ACCEPTED,
        REQUEST_RECEIVED,
        REJECTED
    }

    private int requestId;
    private NotificationType type;
    private String title;
    private String message;
    private String partnerName;
    private String partnerEmail;
    private LocalDateTime timestamp;

    public AppNotification() {
    }

    public AppNotification(int requestId, NotificationType type, String title, String message,
                           String partnerName, String partnerEmail, LocalDateTime timestamp) {
        this.requestId = requestId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.partnerName = partnerName;
        this.partnerEmail = partnerEmail;
        this.timestamp = timestamp;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
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

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
