package com.example.mobilnetestiranjebackend.repositories;

import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.model.Availability;
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


    @Query("SELECT r2 FROM Reservation r1, Reservation r2 " +
            "WHERE r1.status = 3 AND r2.status = 3"+
            "AND r1.accommodation.id = :accommodationId AND r2.accommodation.id = :accommodationId " +
            "AND r2.id != :reservationId AND r1.id = :reservationId " +
            "AND ((" +
            "   r2.reservationStartDate BETWEEN r1.reservationStartDate AND r1.reservationEndDate " +
            "   OR r2.reservationEndDate BETWEEN r1.reservationStartDate AND r1.reservationEndDate " +
            "   OR r2.reservationStartDate <= r1.reservationStartDate AND r2.reservationEndDate >= r1.reservationEndDate" +
            "))"
    )
    List<Reservation> findPendingConflictedReservations(Long accommodationId, Long reservationId);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.id = :reservationId AND r.guest.id = :guestId")
    Optional<Reservation> findByIdAndGuest(Long reservationId, Long guestId);


    @Query("select r from Reservation r where r.guest.id = :guestId and r.accommodation.owner.id = :ownerId and" +
            " r.status = 1 and r.reservationEndDate <= CURRENT_DATE ")
    List<Reservation> findGuestCompletedReservations(Long ownerId, Long guestId);


    List<Reservation> findByAvailability(Availability availability);




//    @Query("select distinct r from Reservation r where r.accommodation.owner.id = :ownerId and " +
//            "(:reservationStatus is null or :reservationStatus = r.status) and " +
//            "(:minDate is null or :minDate >= r.reservationStartDate) and" +
//            "(:maxDate is null or :maxDate >= r.reservationEndDate) and" +
//            "((lower(r.accommodation.address) like LOWER(CONCAT('%', :addressOrName, '%'))) or (lower(r.accommodation.name) like LOWER(CONCAT('%', :addressOrName, '%'))))")
//    List<Reservation> findHostReservations(String addressOrName, LocalDate minDate, LocalDate maxDate, ReservationStatus reservationStatus, Long ownerId);


    @Query("select distinct r from Reservation r where r.accommodation.owner.id = :ownerId and " +
            "(:minDate is null or r.reservationStartDate >= :minDate) and " +
            "(:maxDate is null or r.reservationEndDate <= :maxDate) and " +
            "(:reservationStatus is null or :reservationStatus = r.status) and " +
            "(:addressOrName is null or ((lower(r.accommodation.address) like LOWER(CONCAT('%', :addressOrName, '%'))) or (lower(r.accommodation.name) like LOWER(CONCAT('%', :addressOrName, '%')))))")
    List<Reservation> findHostReservations(LocalDate minDate, LocalDate maxDate, ReservationStatus reservationStatus,String addressOrName, Long ownerId);



    @Query("select distinct r from Reservation r where r.guest.id = :userId and " +
            "(:minDate is null or r.reservationStartDate >= :minDate) and " +
            "(:maxDate is null or r.reservationEndDate <= :maxDate) and " +
            "(:reservationStatus is null or :reservationStatus = r.status) and " +
            "(:addressOrName is null or ((lower(r.accommodation.address) like LOWER(CONCAT('%', :addressOrName, '%'))) or (lower(r.accommodation.name) like LOWER(CONCAT('%', :addressOrName, '%')))))")
    List<Reservation> findGuestReservations(LocalDate minDate, LocalDate maxDate, ReservationStatus reservationStatus, String addressOrName, Long userId);



    @Query("select r from Reservation r where r.id = :reservationId and r.guest.id = :userId and r.status = 0")
    List<Reservation> findGuestCanceledReservations(Long reservationId, Long userId);
}
