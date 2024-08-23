package com.example.projekatmobilne.model.responseDTO;

import com.example.projekatmobilne.model.Notification;

import java.util.List;

public class NotificationListDTO {
    private List<Notification> content;

    public NotificationListDTO() {
    }

    public NotificationListDTO(List<Notification> content) {
        this.content = content;
    }

    public List<Notification> getContent() {
        return content;
    }

    public void setContent(List<Notification> content) {
        this.content = content;
    }
}
