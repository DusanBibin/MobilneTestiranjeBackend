package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    //ako je newAvailId = 0 onda se pravi novi availability i poredi se sa svim periodima, ako je razlicit, onda se poredi
    //samo sa onim periodima koji on nece zameniti u buducnosti
    @Query("SELECT a FROM Availability a " +
            "WHERE a.accommodation.id = :accommodationId AND a.id != :newAvailId " +
            "AND ((:startDate BETWEEN a.startDate AND a.endDate) OR " +
            "(:endDate BETWEEN a.startDate AND a.endDate) OR " +
            "(:startDate <= a.startDate AND :endDate >= a.endDate))")
    List<Availability> findAllByDateRange(Long accommodationId, LocalDate startDate, LocalDate endDate, Long newAvailId);

    Optional<Availability> findById(Long availabilityId);

    @Query("SELECT a FROM Availability a WHERE a.accommodation.id = :accId AND a.id = :availId")
    Optional<Availability> findByIdAndAccommodationId(Long availId, Long accId);
    List<Availability> findAllByAccommodationId(Long accommodationId);

}
