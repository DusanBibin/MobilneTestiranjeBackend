package com.example.projekatmobilne.model.responseDTO;

public class NotificationDTO {
    private String userId;
    private String title;
    private String content;

    public NotificationDTO() {
    }

    public NotificationDTO(String userId, String title, String content) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
