package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.Notification;
import com.example.mobilnetestiranjebackend.model.NotificationType;
import com.example.mobilnetestiranjebackend.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void toggleNotificationReadStatus(Long notificationId) {
        Optional<Notification> notificationWrapper = notificationRepository.findById(notificationId);
        if (notificationWrapper.isEmpty())
            throw new NonExistingEntityException("Notification with this id doesn't exist");
        Notification notification = notificationWrapper.get();
        notification.setIsRead(!notification.getIsRead());
        notificationRepository.save(notification);
    }

    public Notification createNotification(Long userId, String message, NotificationType notificationType) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .isRead(false)
                .notificationType(notificationType)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
