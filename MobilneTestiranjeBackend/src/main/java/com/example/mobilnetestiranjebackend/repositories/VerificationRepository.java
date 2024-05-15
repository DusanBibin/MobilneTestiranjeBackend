package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.Verification;
import com.example.mobilnetestiranjebackend.model.VerificationEmailChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VerificationRepository extends JpaRepository<VerificationEmailChange, Long> {

    @Query("select g from Guest g join g.favorites f where f.id = :accommodationId")
    List<Guest> findByFavoriteAccommodationId(Long accommodationId);



    @Query("select v from User u join u.emailChangeVerification v where u.id = :userId")
    Optional<VerificationEmailChange> findById(Long userId);
}
