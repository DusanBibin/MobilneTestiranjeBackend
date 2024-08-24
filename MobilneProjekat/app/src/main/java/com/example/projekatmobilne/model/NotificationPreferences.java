package com.example.projekatmobilne.model;

import com.example.projekatmobilne.model.Enum.NotificationType;
import com.google.gson.annotations.SerializedName;

public class NotificationPreferences {
    private Long id;
    private Long userId;

    @SerializedName("notificationType")
    private NotificationType notificationType;

    @SerializedName("isEnabled")
    private Boolean isEnabled;

    // Getters and setters
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

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}