package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Verification;
import com.example.mobilnetestiranjebackend.model.VerificationEmailChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    @Query("select v from User u join u.verification v where u.id = :userId")
    Optional<Verification> findByUserId(Long userId);
}
