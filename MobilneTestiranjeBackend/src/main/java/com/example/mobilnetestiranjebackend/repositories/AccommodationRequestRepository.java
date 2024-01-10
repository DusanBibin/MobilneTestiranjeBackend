package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.AccommodationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccommodationRequestRepository extends JpaRepository<AccommodationRequest, Integer> {
    Optional<AccommodationRequest> findById(Long requestId);
}
