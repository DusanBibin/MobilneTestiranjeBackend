package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.AccommodationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationRequestRepository extends JpaRepository<AccommodationRequest, Integer> {
}
