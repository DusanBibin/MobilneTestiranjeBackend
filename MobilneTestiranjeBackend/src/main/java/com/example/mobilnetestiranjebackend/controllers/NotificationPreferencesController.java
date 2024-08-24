// NotificationPreferencesController.java
package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.model.NotificationPreferences;
import com.example.mobilnetestiranjebackend.model.NotificationType;
import com.example.mobilnetestiranjebackend.services.NotificationPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification-preferences")
@RequiredArgsConstructor
public class NotificationPreferencesController {
    private final NotificationPreferencesService notificationPreferencesService;

    @PutMapping("/{userId}/{notificationType}")
    public ResponseEntity<NotificationPreferences> updateNotificationPreference(
            @PathVariable Long userId,
            @PathVariable NotificationType notificationType,
            @RequestParam Boolean isEnabled) {
        NotificationPreferences updatedPreference = notificationPreferencesService.updateNotificationPreference(userId, notificationType, isEnabled);
        return new ResponseEntity<>(updatedPreference, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationPreferences>> getNotificationPreferencesByUserId(@PathVariable Long userId) {
        List<NotificationPreferences> preferences = notificationPreferencesService.getNotificationPreferencesByUserId(userId);
        return new ResponseEntity<>(preferences, HttpStatus.OK);
    }
}