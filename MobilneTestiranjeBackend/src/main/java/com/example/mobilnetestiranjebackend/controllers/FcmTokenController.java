// FcmTokenController.java
package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.model.FcmToken;
import com.example.mobilnetestiranjebackend.services.FcmTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/fcm-token")
public class FcmTokenController {
    private final FcmTokenService fcmTokenService;

    public FcmTokenController(FcmTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }

    @PostMapping
    public ResponseEntity<FcmToken> saveToken(@RequestParam Long userId, @RequestParam String token) {
        FcmToken savedToken = fcmTokenService.saveToken(userId, token);
        return ResponseEntity.ok(savedToken);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<FcmToken> getTokenByUserId(@PathVariable Long userId) {
        Optional<FcmToken> token = fcmTokenService.getTokenByUserId(userId);
        return token.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}