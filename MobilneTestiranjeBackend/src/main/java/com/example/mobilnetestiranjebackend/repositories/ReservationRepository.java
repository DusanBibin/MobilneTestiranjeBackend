package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.id = :reservationId AND r.accommodation.id = :accommodationId")
    Optional<Reservation> findByIdAndAccommodation(Long accommodationId, Long reservationId);





    @Query("SELECT r FROM Reservation r WHERE r.accommodation.id = :accommodationId AND r.status = 1 AND CURRENT_DATE <= r.reservationEndDate")
    List<Reservation> findReservationsNotEndedByAccommodationId(Long accommodationId);


    @Query("select r from  Reservation r where r.accommodation.id = :accomId and r.availability.id = :availId and r.status = 1 and CURRENT_DATE <= r.reservationEndDate")
    List<Reservation> findReservationNotEndedByAvailabilityIdAndAccommodationId(Long accomId, Long availId);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.status = 1 " +
            "AND r.accommodation.id = :accomId " +
            "AND r.availability.id = :availId " +
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



    @Query("SELECT r FROM Reservation r " +
            "WHERE r.status = 3 " +
            "AND r.accommodation.id = :accommodationId " +
            "AND r.id != :reservationId " +
            "AND ((" +
            "   :startDate BETWEEN r.reservationStartDate AND r.reservationEndDate " +
            "   OR :endDate BETWEEN r.reservationStartDate AND r.reservationEndDate " +
            "   OR :startDate <= r.reservationStartDate AND :endDate >= r.reservationEndDate" +
            "))"
    )
    List<Reservation> findPendingConflictedReservations(Long accommodationId, Long reservationId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.id = :reservationId AND r.guest.id = :guestId")
    Optional<Reservation> findByIdAndGuest(Long reservationId, Long guestId);


}
