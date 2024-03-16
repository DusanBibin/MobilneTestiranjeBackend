package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);


    @Query("select u from User u where u.id = :userId")
    Optional<User> findByUserId(Long userId);


    Optional<User> findUserByVerification_VerificationCode(String code);
}
