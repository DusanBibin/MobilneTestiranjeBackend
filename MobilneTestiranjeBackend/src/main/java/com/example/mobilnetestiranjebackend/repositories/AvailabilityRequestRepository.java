package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.AccommodationRequest;
import com.example.mobilnetestiranjebackend.model.Availability;
import com.example.mobilnetestiranjebackend.model.AvailabilityRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AvailabilityRequestRepository extends JpaRepository<AvailabilityRequest, Long> {
    List<AvailabilityRequest> findByAvailability(Availability availability);
//    @Query("select ar from AvailabilityRequest ar where ar.id = :requestId and ar.accommodation.id = :accommodationId")
//    Optional<AvailabilityRequest> findByIdAAndAccommodation(Long requestId, Long accommodationId);
}
