package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.NotificationPreferences;
import com.example.mobilnetestiranjebackend.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {
    Optional<NotificationPreferences> findByUserIdAndNotificationType(Long userId, NotificationType notificationType);

    List<NotificationPreferences> findByUserId(Long userId);
}
