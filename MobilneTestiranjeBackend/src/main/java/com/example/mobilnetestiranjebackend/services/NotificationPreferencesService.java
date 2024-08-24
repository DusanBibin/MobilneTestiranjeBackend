// NotificationPreferencesService.java
package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.model.NotificationPreferences;
import com.example.mobilnetestiranjebackend.model.NotificationType;
import com.example.mobilnetestiranjebackend.repositories.NotificationPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationPreferencesService {
    private final NotificationPreferencesRepository notificationPreferencesRepository;

    public NotificationPreferences updateNotificationPreference(Long userId, NotificationType notificationType, Boolean isEnabled) {
        NotificationPreferences preference = notificationPreferencesRepository.findByUserIdAndNotificationType(userId, notificationType)
                .orElseThrow(() -> new RuntimeException("Notification preference not found"));
        preference.setIsEnabled(isEnabled);
        return notificationPreferencesRepository.save(preference);
    }

    public List<NotificationPreferences> getNotificationPreferencesByUserId(Long userId) {
        return notificationPreferencesRepository.findByUserId(userId);
    }
}