package com.example.projekatmobilne.model;

import com.example.projekatmobilne.model.Enum.NotificationType;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private Long userId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;

    private String notificationType;

    public Notification() {
    }

    public Notification(Long id, Long userId, String message, Boolean isRead, LocalDateTime createdAt, String notificationType) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.notificationType = notificationType;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}