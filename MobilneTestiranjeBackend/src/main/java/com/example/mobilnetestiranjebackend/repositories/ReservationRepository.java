package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.accommodation.id = :accommodationId AND r.status = 1 AND r.reservationEndDate >= CURRENT_DATE")
    List<Reservation> findReservationsNotEndedByAccommodationId(Long accommodationId);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.status = 1 " +
            "AND r.accommodation.id = :accomId " +
            "AND r.accommodationAvailability.id = :availId " +
            "AND ((" +
            "   :startDate BETWEEN r.reservationStartDate AND r.reservationEndDate " +
            "   OR :endDate BETWEEN r.reservationStartDate AND r.reservationEndDate " +
            "   OR :startDate <= r.reservationStartDate AND :endDate >= r.reservationEndDate" +
            "))"
    )
    List<Reservation> findAcceptedReservationsInConflict(
             LocalDate startDate,
             LocalDate endDate,
             Long accomId,
             Long availId
    );

}
