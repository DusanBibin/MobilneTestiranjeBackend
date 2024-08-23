package com.example.mobilnetestiranjebackend.DTOs;

import com.example.mobilnetestiranjebackend.model.Notification;

import java.util.List;

public class NotificationListDTO {
    private List<Notification> content;

    public NotificationListDTO(List<Notification> notifications) {
        this.content = notifications;
    }

    public List<Notification> getContent() {
        return content;
    }

    public void setContent(List<Notification> content) {
        this.content = content;
    }
}
