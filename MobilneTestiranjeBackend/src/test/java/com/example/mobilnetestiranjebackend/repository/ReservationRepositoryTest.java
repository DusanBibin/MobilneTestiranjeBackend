package com.example.mobilnetestiranjebackend.repository;

import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
@ActiveProfiles("test")
public class ReservationRepositoryTest {

    @Autowired
    private AccommodationRepository accommodationRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void testFindByIdAndAccommodationSuccess(){

        var accommodation = Accommodation.builder()
                .id(1L)
                .reservations(new ArrayList<>())
                .build();

        accommodationRepository.save(accommodation);

        var reservation1 = Reservation.builder()
                .id(1L)
                .accommodation(accommodation)
                .build();
        reservationRepository.save(reservation1);
        accommodation.getReservations().add(reservation1);
        accommodationRepository.save(accommodation);


        Optional<Reservation> foundReservation = reservationRepository.findByIdAndAccommodation(1L, 1L);
        System.out.println(reservationRepository.findAll().size());
        assertThat(foundReservation).isPresent();
        Reservation reservation = foundReservation.get();
        assertThat(reservation.getId()).isEqualTo(1L);
        assertThat(reservation.getAccommodation().getId()).isEqualTo(1L);
        assertThat(accommodation.getReservations().get(0).getId()).isEqualTo(1L);
    }

    @Test
    public void testFindByIdAndAccommodationFailAccomId(){


        var accommodation = Accommodation.builder()
                .id(1L)
                .reservations(new ArrayList<>())
                .build();

        accommodationRepository.save(accommodation);

        var reservation1 = Reservation.builder()
                .id(1L)
                .accommodation(accommodation)
                .build();
        reservationRepository.save(reservation1);
        accommodation.getReservations().add(reservation1);
        accommodationRepository.save(accommodation);



        System.out.println(reservationRepository.findAll().size());
        Optional<Reservation> foundReservation = reservationRepository.findByIdAndAccommodation(2L, 1L);
        assertThat(foundReservation).isEmpty();
    }

    @Test
    public void testFindByIdAndAccommodationFailReservationId(){

        var accommodation = Accommodation.builder()
                .id(1L)
                .reservations(new ArrayList<>())
                .build();

        accommodationRepository.save(accommodation);

        var reservation1 = Reservation.builder()
                .id(1L)
                .accommodation(accommodation)
                .build();
        reservationRepository.save(reservation1);
        accommodation.getReservations().add(reservation1);
        accommodationRepository.save(accommodation);



        System.out.println(reservationRepository.findAll().size());

        Optional<Reservation> foundReservation = reservationRepository.findByIdAndAccommodation(1L, 2L);
        assertThat(foundReservation).isEmpty();
    }

}
