package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);


    Optional<User> findUserByVerification_VerificationCode(String code);
}
