// NotificationDTO.java
package com.example.mobilnetestiranjebackend.DTOs;

import com.example.mobilnetestiranjebackend.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String userId;
    private String title;
    private String content;

    public static List<NotificationDTO> fromEntityList(List<Notification> notifications) {
        return notifications.stream()
                .map(notification -> new NotificationDTO(
                        notification.getUserId().toString(),
                        notification.getNotificationType().name(),
                        notification.getMessage()
                ))
                .collect(Collectors.toList());
    }
}