package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT r FROM Reservation r WHERE r.accommodation.id = :accommodationId AND r.status = 1 AND r.reservationEndDate >= CURRENT_DATE")
    List<Reservation> findReservationsNotEndedByAccommodationId(Long accommodationId);

}
