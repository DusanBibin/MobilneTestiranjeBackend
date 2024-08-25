// FcmTokenRepository.java
package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    Optional<FcmToken> findByUserId(Long userId);
}