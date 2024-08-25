// FcmTokenService.java
package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.model.FcmToken;
import com.example.mobilnetestiranjebackend.repositories.FcmTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FcmTokenService {
    private final FcmTokenRepository fcmTokenRepository;

    public FcmTokenService(FcmTokenRepository fcmTokenRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
    }

    public FcmToken saveToken(Long userId, String token) {
        Optional<FcmToken> existingToken = fcmTokenRepository.findByUserId(userId);
        FcmToken fcmToken;
        if (existingToken.isPresent()) {
            fcmToken = existingToken.get();
            fcmToken.setToken(token);
        } else {
            fcmToken = new FcmToken();
            fcmToken.setUserId(userId);
            fcmToken.setToken(token);
        }
        return fcmTokenRepository.save(fcmToken);
    }

    public Optional<FcmToken> getTokenByUserId(Long userId) {
        return fcmTokenRepository.findByUserId(userId);
    }
}