package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.AccommodationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccommodationRequestRepository extends JpaRepository<AccommodationRequest, Long> {
    Optional<AccommodationRequest> findById(Long requestId);



}
