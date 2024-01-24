package com.example.mobilnetestiranjebackend.repository;

import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.AccommodationAvailability;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.AvailabilityRepository;
import com.example.mobilnetestiranjebackend.repositories.ReservationRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@ActiveProfiles("test")
public class ReservationRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Test
    public void testFindByIdAndAccommodation_Success(){

        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var reservation1 = Reservation.builder()
                .accommodation(accommodation)
                .build();

        reservation1 = reservationRepository.save(reservation1);
        accommodation.getReservations().add(reservation1);
        accommodationRepository.save(accommodation);


        Optional<Reservation> foundReservation = reservationRepository.findByIdAndAccommodation(accommodation.getId(), reservation1.getId());

        assertThat(foundReservation).isPresent();
        Reservation reservationCheck = foundReservation.get();
        assertThat(reservationCheck.getId()).isEqualTo(reservation1.getId());
        assertThat(reservationCheck.getAccommodation().getId()).isEqualTo(accommodation.getId());
        assertThat(accommodation.getReservations().get(0).getId()).isEqualTo(reservation1.getId());
    }

    @Test
    public void testFindByIdAndAccommodation_FailAccomId(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .build();

        accommodationRepository.save(accommodation);

        var reservation1 = Reservation.builder()
                .accommodation(accommodation)
                .build();
        reservation1 = reservationRepository.save(reservation1);
        accommodation.getReservations().add(reservation1);
        accommodationRepository.save(accommodation);



        System.out.println(reservationRepository.findAll().size());
        Optional<Reservation> foundReservation = reservationRepository.findByIdAndAccommodation(0L, reservation1.getId());
        assertThat(foundReservation).isEmpty();
    }

    @Test
    public void testFindByIdAndAccommodation_FailReservationId(){

        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var reservation1 = Reservation.builder()
                .accommodation(accommodation)
                .build();
        reservationRepository.save(reservation1);
        accommodation.getReservations().add(reservation1);
        accommodationRepository.save(accommodation);



        Optional<Reservation> foundReservation = reservationRepository.findByIdAndAccommodation(accommodation.getId(), 0L);
        assertThat(foundReservation).isEmpty();
    }




    @Test void testFindAcceptedReservationsInConflict_NoConflict(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.ACCEPTED)
                .build();

        reservation1 = reservationRepository.save(reservation1);


        accommodation.getReservations().add(reservation1);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        LocalDate date1 = LocalDate.now().plusDays(2);
        LocalDate date2 = LocalDate.now().plusDays(7);
        List<Reservation> conflicted = reservationRepository.findAcceptedReservationsInConflict(date1, date2, accommodation.getId(), availability.getId());

        assertThat(conflicted.size()).isEqualTo(0);
    }

    @Test void testFindAcceptedReservationsInConflict_EndDateInBetween(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.ACCEPTED)
                .build();

        reservation1 = reservationRepository.save(reservation1);


        accommodation.getReservations().add(reservation1);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        LocalDate date1 = LocalDate.now().plusDays(2);
        LocalDate date2 = LocalDate.now().plusDays(9);
        List<Reservation> conflicted = reservationRepository.findAcceptedReservationsInConflict(date1, date2, accommodation.getId(), availability.getId());

        assertThat(conflicted.size()).isEqualTo(1);
        assertThat(conflicted.get(0).getId()).isEqualTo(reservation1.getId());
    }

    @Test void testFindAcceptedReservationsInConflict_StartDateInBetween(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.ACCEPTED)
                .build();

        reservation1 = reservationRepository.save(reservation1);


        accommodation.getReservations().add(reservation1);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        LocalDate date1 = LocalDate.now().plusDays(9);
        LocalDate date2 = LocalDate.now().plusDays(15);
        List<Reservation> conflicted = reservationRepository.findAcceptedReservationsInConflict(date1, date2, accommodation.getId(), availability.getId());

        assertThat(conflicted.size()).isEqualTo(1);
        assertThat(conflicted.get(0).getId()).isEqualTo(reservation1.getId());
    }


    @Test void testFindAcceptedReservationsInConflict_EncompassingPeriod(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.ACCEPTED)
                .build();

        reservation1 = reservationRepository.save(reservation1);


        accommodation.getReservations().add(reservation1);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        LocalDate date1 = LocalDate.now().plusDays(6);
        LocalDate date2 = LocalDate.now().plusDays(15);
        List<Reservation> conflicted = reservationRepository.findAcceptedReservationsInConflict(date1, date2, accommodation.getId(), availability.getId());

        assertThat(conflicted.size()).isEqualTo(1);
        assertThat(conflicted.get(0).getId()).isEqualTo(reservation1.getId());
    }

    @Test void testFindAcceptedReservationsInConflict_NonAcceptedStatusConflictDates(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.PENDING)
                .build();

        reservation1 = reservationRepository.save(reservation1);


        accommodation.getReservations().add(reservation1);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        LocalDate date1 = LocalDate.now().plusDays(2);
        LocalDate date2 = LocalDate.now().plusDays(9);
        List<Reservation> conflicted = reservationRepository.findAcceptedReservationsInConflict(date1, date2, accommodation.getId(), availability.getId());

        assertThat(conflicted.size()).isEqualTo(0);
    }


    @Test void testFindAcceptedReservationsInConflict_NonExistingAccommodation(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.ACCEPTED)
                .build();

        reservation1 = reservationRepository.save(reservation1);


        accommodation.getReservations().add(reservation1);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        LocalDate date1 = LocalDate.now().plusDays(2);
        LocalDate date2 = LocalDate.now().plusDays(9);
        List<Reservation> conflicted = reservationRepository.findAcceptedReservationsInConflict(date1, date2, 0L, availability.getId());

        assertThat(conflicted.size()).isEqualTo(0);
    }

    @Test void testFindAcceptedReservationsInConflict_NonExistingAvailability(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.ACCEPTED)
                .build();

        reservation1 = reservationRepository.save(reservation1);


        accommodation.getReservations().add(reservation1);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        LocalDate date1 = LocalDate.now().plusDays(2);
        LocalDate date2 = LocalDate.now().plusDays(9);
        List<Reservation> conflicted = reservationRepository.findAcceptedReservationsInConflict(date1, date2, accommodation.getId(), 0L);

        assertThat(conflicted.size()).isEqualTo(0);
    }

    @Test void testFindAcceptedReservationsInConflict_EdgeDate(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.ACCEPTED)
                .build();

        reservation1 = reservationRepository.save(reservation1);


        accommodation.getReservations().add(reservation1);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);


        LocalDate date1 = LocalDate.now().plusDays(2);
        LocalDate date2 = LocalDate.now().plusDays(8);
        List<Reservation> conflicted = reservationRepository.findAcceptedReservationsInConflict(date1, date2, accommodation.getId(), availability.getId());

        assertThat(conflicted.size()).isEqualTo(1);
        assertThat(conflicted.get(0).getId()).isEqualTo(reservation1.getId());
    }



    @Test void testFindPendingConflictedReservations_NoConflict(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.PENDING)
                .build();

        reservation1 = reservationRepository.save(reservation1);

        var reservation2 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(5))
                .reservationEndDate(LocalDate.now().plusDays(7))
                .status(ReservationStatus.PENDING)
                .build();

        reservation2 = reservationRepository.save(reservation2);


        accommodation.getReservations().add(reservation1);
        accommodation.getReservations().add(reservation2);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);

        List<Reservation> conflicted = reservationRepository
                .findPendingConflictedReservations(accommodation.getId(), reservation1.getId(),
                        reservation1.getReservationStartDate(), reservation1.getReservationEndDate());

        assertThat(conflicted.size()).isEqualTo(0);

    }

    @Test void testFindPendingConflictedReservations_StartDateInBetween(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(6))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.PENDING)
                .build();

        reservation1 = reservationRepository.save(reservation1);

        var reservation2 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(5))
                .reservationEndDate(LocalDate.now().plusDays(7))
                .status(ReservationStatus.PENDING)
                .build();

        reservation2 = reservationRepository.save(reservation2);


        accommodation.getReservations().add(reservation1);
        accommodation.getReservations().add(reservation2);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);

        List<Reservation> conflicted = reservationRepository
                .findPendingConflictedReservations(accommodation.getId(), reservation1.getId(),
                        reservation1.getReservationStartDate(), reservation1.getReservationEndDate());

        assertThat(conflicted.size()).isEqualTo(1);
        assertThat(conflicted.get(0).getId()).isEqualTo(reservation2.getId());
    }

    @Test void testFindPendingConflictedReservations_EndDateInBetween(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(4))
                .reservationEndDate(LocalDate.now().plusDays(6))
                .status(ReservationStatus.PENDING)
                .build();

        reservation1 = reservationRepository.save(reservation1);

        var reservation2 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(5))
                .reservationEndDate(LocalDate.now().plusDays(7))
                .status(ReservationStatus.PENDING)
                .build();

        reservation2 = reservationRepository.save(reservation2);


        accommodation.getReservations().add(reservation1);
        accommodation.getReservations().add(reservation2);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);

        List<Reservation> conflicted = reservationRepository
                .findPendingConflictedReservations(accommodation.getId(), reservation1.getId(),
                        reservation1.getReservationStartDate(), reservation1.getReservationEndDate());

        assertThat(conflicted.size()).isEqualTo(1);
        assertThat(conflicted.get(0).getId()).isEqualTo(reservation2.getId());
    }

    @Test void testFindPendingConflictedReservations_EncompassingPeriod(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(4))
                .reservationEndDate(LocalDate.now().plusDays(8))
                .status(ReservationStatus.PENDING)
                .build();

        reservation1 = reservationRepository.save(reservation1);

        var reservation2 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(5))
                .reservationEndDate(LocalDate.now().plusDays(7))
                .status(ReservationStatus.PENDING)
                .build();

        reservation2 = reservationRepository.save(reservation2);


        accommodation.getReservations().add(reservation1);
        accommodation.getReservations().add(reservation2);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);

        List<Reservation> conflicted = reservationRepository
                .findPendingConflictedReservations(accommodation.getId(), reservation1.getId(),
                        reservation1.getReservationStartDate(), reservation1.getReservationEndDate());

        assertThat(conflicted.size()).isEqualTo(1);
        assertThat(conflicted.get(0).getId()).isEqualTo(reservation2.getId());
    }

    @Test void testFindPendingConflictedReservations_EdgeDate(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(4))
                .reservationEndDate(LocalDate.now().plusDays(8))
                .status(ReservationStatus.PENDING)
                .build();

        reservation1 = reservationRepository.save(reservation1);

        var reservation2 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.PENDING)
                .build();

        reservation2 = reservationRepository.save(reservation2);


        accommodation.getReservations().add(reservation1);
        accommodation.getReservations().add(reservation2);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);

        List<Reservation> conflicted = reservationRepository
                .findPendingConflictedReservations(accommodation.getId(), reservation1.getId(),
                        reservation1.getReservationStartDate(), reservation1.getReservationEndDate());

        assertThat(conflicted.size()).isEqualTo(1);
        assertThat(conflicted.get(0).getId()).isEqualTo(reservation2.getId());
    }

    @Test void testFindPendingConflictedReservations_NonPendingStatusConflictDates(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(4))
                .reservationEndDate(LocalDate.now().plusDays(9))
                .status(ReservationStatus.PENDING)
                .build();

        reservation1 = reservationRepository.save(reservation1);

        var reservation2 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.CANCELED)
                .build();

        reservation2 = reservationRepository.save(reservation2);


        accommodation.getReservations().add(reservation1);
        accommodation.getReservations().add(reservation2);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);

        List<Reservation> conflicted = reservationRepository
                .findPendingConflictedReservations(accommodation.getId(), reservation1.getId(),
                        reservation1.getReservationStartDate(), reservation1.getReservationEndDate());

        assertThat(conflicted.size()).isEqualTo(0);
    }

    @Test void testFindPendingConflictedReservations_NonExistingAccommodation(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(4))
                .reservationEndDate(LocalDate.now().plusDays(9))
                .status(ReservationStatus.PENDING)
                .build();

        reservation1 = reservationRepository.save(reservation1);

        var reservation2 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.PENDING)
                .build();

        reservation2 = reservationRepository.save(reservation2);


        accommodation.getReservations().add(reservation1);
        accommodation.getReservations().add(reservation2);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);

        List<Reservation> conflicted = reservationRepository
                .findPendingConflictedReservations(0L, reservation1.getId(),
                        reservation1.getReservationStartDate(), reservation1.getReservationEndDate());

        assertThat(conflicted.size()).isEqualTo(0);
    }

    @Test void testFindPendingConflictedReservations_NonExistingReservation(){
        var accommodation = Accommodation.builder()
                .reservations(new ArrayList<>())
                .availabilityList(new ArrayList<>())
                .build();

        accommodation = accommodationRepository.save(accommodation);

        var availability = AccommodationAvailability.builder()
                .accommodation(accommodation)
                .build();

        availability = availabilityRepository.save(availability);

        var reservation1 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(4))
                .reservationEndDate(LocalDate.now().plusDays(9))
                .status(ReservationStatus.PENDING)
                .build();

        reservation1 = reservationRepository.save(reservation1);

        var reservation2 = Reservation.builder()
                .accommodationAvailability(availability)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(8))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.PENDING)
                .build();

        reservation2 = reservationRepository.save(reservation2);


        accommodation.getReservations().add(reservation1);
        accommodation.getReservations().add(reservation2);
        accommodation.getAvailabilityList().add(availability);
        accommodationRepository.save(accommodation);

        List<Reservation> conflicted = reservationRepository
                .findPendingConflictedReservations(accommodation.getId(), 0L,
                        reservation1.getReservationStartDate(), reservation1.getReservationEndDate());

        assertThat(conflicted.size()).isEqualTo(2);
    }

    @Test
    public void shouldFindReservationsNotEndedByAccommodationId() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();
        Reservation reservation = new Reservation();
        reservation.setAccommodation(accommodation);
        reservation.setStatus(ReservationStatus.ACCEPTED);
        reservation.setReservationEndDate(LocalDate.now().plusDays(20));
        reservation = reservationRepository.save(reservation);
        reservationRepository.flush();
        List<Reservation> reservations = reservationRepository.findReservationsNotEndedByAccommodationId(accommodation.getId());
        assertFalse(reservations.isEmpty());
    }

    @Test
    public void shouldNotFindReservationsNotEndedByAccommodationIdWrongAccommodation() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        Accommodation accommodation2 = new Accommodation();
        accommodation2 = accommodationRepository.save(accommodation2);
        accommodationRepository.flush();
        Reservation reservation = new Reservation();
        reservation.setAccommodation(accommodation);
        reservation.setStatus(ReservationStatus.ACCEPTED);
        reservation.setReservationEndDate(LocalDate.now().plusDays(20));
        reservation = reservationRepository.save(reservation);
        reservationRepository.flush();
        List<Reservation> reservations = reservationRepository.findReservationsNotEndedByAccommodationId(accommodation2.getId());
        assertTrue(reservations.isEmpty());
    }

    @Test
    public void shouldNotFindReservationsNotEndedByAccommodationIdWrongStatus() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();
        Reservation reservation = new Reservation();
        reservation.setAccommodation(accommodation);
        reservation.setStatus(ReservationStatus.CANCELED);
        reservation.setReservationEndDate(LocalDate.now().plusDays(20));
        reservation = reservationRepository.save(reservation);
        reservationRepository.flush();
        List<Reservation> reservations = reservationRepository.findReservationsNotEndedByAccommodationId(accommodation.getId());
        assertTrue(reservations.isEmpty());
    }

    @Test
    public void shouldNotFindReservationsNotEndedByAccommodationIdWrongDate() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();
        Reservation reservation = new Reservation();
        reservation.setAccommodation(accommodation);
        reservation.setStatus(ReservationStatus.ACCEPTED);
        reservation.setReservationEndDate(LocalDate.now().minusDays(20));
        reservation = reservationRepository.save(reservation);
        reservationRepository.flush();
        List<Reservation> reservations = reservationRepository.findReservationsNotEndedByAccommodationId(accommodation.getId());
        assertTrue(reservations.isEmpty());
    }

    @Test
    public void shouldFindReservationsNotEndedByAccommodationIdToday() {
        Accommodation accommodation = new Accommodation();
        accommodation = accommodationRepository.save(accommodation);
        accommodationRepository.flush();
        Reservation reservation = new Reservation();
        reservation.setAccommodation(accommodation);
        reservation.setStatus(ReservationStatus.ACCEPTED);
        reservation.setReservationEndDate(LocalDate.now());
        reservation = reservationRepository.save(reservation);
        reservationRepository.flush();
        List<Reservation> reservations = reservationRepository.findReservationsNotEndedByAccommodationId(accommodation.getId());
        assertFalse(reservations.isEmpty());
    }


}
