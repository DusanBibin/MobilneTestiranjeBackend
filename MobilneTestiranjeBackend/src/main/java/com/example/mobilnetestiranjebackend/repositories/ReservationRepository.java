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


    @Query("SELECT r FROM Reservation r WHERE r.accommodation.id = :accommodationId AND r.status = 1 AND CURRENT_DATE > r.reservationEndDate")
    List<Reservation> findReservationsEndedByAccommodationId(Long accommodationId);


    @Query("SELECT r FROM Reservation r WHERE r.guest.id = :guestId AND r.status = 1 AND CURRENT_DATE <= r.reservationEndDate")
    List<Reservation> findReservationsNotEndedByGuestId(Long guestId);

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

    //TODO VIDETI DA LI JE POTREBNO UVODITI FINISHED STATUS I DA LI JE POTREBNO NA SVAKO LOGOVANJE PROVERAVATI DATUM DA LI SE REZERVACIJA ZAVRSILA I DA LI JE
    //VREME ZA ODGOVARANJE NA RESERVATION REQUEST PROSLO
    @Query("select r from Reservation  r where (r.status = 1 AND CURRENT_DATE <= r.reservationEndDate or r.status = 3 ) and r.guest.id = :guestId and ((" +
            "               :startDate BETWEEN r.reservationStartDate AND r.reservationEndDate " +
            "               OR :endDate BETWEEN r.reservationStartDate AND r.reservationEndDate " +
            "               OR :startDate <= r.reservationStartDate AND :endDate >= r.reservationEndDate))")
    List<Reservation> findGuestReservationsInConflict(LocalDate startDate, LocalDate endDate, Long guestId);



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


    @Query("select r from Reservation r where r.guest.id = :guestId and r.accommodation.owner.id = :ownerId and" +
            " r.status = 1 and r.reservationEndDate <= CURRENT_DATE ")
    List<Reservation> findGuestCompletedReservations(Long ownerId, Long guestId);


}
