package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.AccommodationAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<AccommodationAvailability, Integer> {


    @Query("SELECT a FROM AccommodationAvailability a " +
            "WHERE a.accommodation.id = :accommodationId " +
            "AND ((:startDate BETWEEN a.startDate AND a.endDate) OR " +
            "(:endDate BETWEEN a.startDate AND a.endDate) OR " +
            "(:startDate <= a.startDate AND :endDate >= a.endDate))")
    List<AccommodationAvailability> findAllByDateRange(Long accommodationId, LocalDate startDate, LocalDate endDate);

}
