package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.AvailabilityRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityRequestRepository extends JpaRepository<AvailabilityRequest, Integer> {
}
