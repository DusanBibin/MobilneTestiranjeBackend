package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.NotificationDTO;
import com.example.mobilnetestiranjebackend.DTOs.NotificationListDTO;
import com.example.mobilnetestiranjebackend.model.Notification;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PreAuthorize("hasAuthority('OWNER') or hasAuthority('GUEST')")
    @PutMapping("/{id}/toggle-read")
    public ResponseEntity<Void> toggleNotificationReadStatus(@PathVariable Long id,
                                                             @AuthenticationPrincipal User user) {
        notificationService.toggleNotificationReadStatus(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('OWNER') or hasAuthority('GUEST')")
    @GetMapping("/user")
    public ResponseEntity<?> getNotificationsByUserId(@AuthenticationPrincipal User user) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(user.getId());
        NotificationListDTO notificationListDTO = new NotificationListDTO(notifications);
        return ResponseEntity.ok().body(notificationListDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotifications(@PathVariable String userId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
}