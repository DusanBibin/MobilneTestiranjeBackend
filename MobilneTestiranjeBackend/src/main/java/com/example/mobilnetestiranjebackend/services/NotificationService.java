package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.NotificationDTO;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.FcmToken;
import com.example.mobilnetestiranjebackend.model.Notification;
import com.example.mobilnetestiranjebackend.model.NotificationType;
import com.example.mobilnetestiranjebackend.repositories.FcmTokenRepository;
import com.example.mobilnetestiranjebackend.repositories.NotificationRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final FcmTokenRepository fcmTokenRepository;

    public NotificationService(NotificationRepository notificationRepository, FcmTokenRepository fcmTokenRepository) {
        this.notificationRepository = notificationRepository;
        this.fcmTokenRepository = fcmTokenRepository;
    }

    public void toggleNotificationReadStatus(Long notificationId) {
        Optional<Notification> notificationWrapper = notificationRepository.findById(notificationId);
        if (notificationWrapper.isEmpty())
            throw new NonExistingEntityException("Notification with this id doesn't exist");
        Notification notification = notificationWrapper.get();
        notification.setIsRead(!notification.getIsRead());
        notificationRepository.save(notification);
    }

    public Notification createNotification(Long userId, String message, NotificationType notificationType) {
        Notification notification = new Notification(userId, message, false, LocalDateTime.now(), notificationType);
        notification = notificationRepository.save(notification);
        FcmToken fcmToken = fcmTokenRepository.findByUserId(userId).get();
        sendNotification(fcmToken.getToken(), "You have a new notification!", message);
        return notification;
    }

    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<NotificationDTO> getNotificationsForUser(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(Long.parseLong(userId));
        return NotificationDTO.fromEntityList(notifications);
    }

    public void sendNotification(String token, String title, String body) {
        com.google.firebase.messaging.Notification notification = com.google.firebase.messaging.Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }
}