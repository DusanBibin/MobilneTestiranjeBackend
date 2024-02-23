package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.AccommodationReview;
import com.example.mobilnetestiranjebackend.model.AvailabilityRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccommodationReviewRepository extends JpaRepository<AccommodationReview, Long> {
}
