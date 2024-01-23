package com.example.mobilnetestiranjebackend.service;


import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.repositories.ReservationRepository;
import com.example.mobilnetestiranjebackend.services.ReservationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private final Long VALID_ACCOMMODATION_ID = 1L;
    private final Long VALID_RESERVATION_ID = 2L;

    @Test
    void testFindReservationByIdAccommodation() {

        Reservation mockReservation = new Reservation();
        Mockito.when(reservationRepository.findByIdAndAccommodation(VALID_ACCOMMODATION_ID, VALID_RESERVATION_ID))
                .thenReturn(Optional.of(mockReservation));

        Optional<Reservation> result = reservationService.findReservationByIdAccommodation(VALID_ACCOMMODATION_ID, VALID_RESERVATION_ID);

        assert(result.isPresent());
    }

    @Test
    void testFindReservationByIdAccommodation_InvalidAccommodationId() {

        Reservation mockReservation = new Reservation();
        Mockito.when(reservationRepository.findByIdAndAccommodation(VALID_ACCOMMODATION_ID, VALID_RESERVATION_ID))
                .thenReturn(Optional.of(mockReservation));

        Optional<Reservation> result = reservationService.findReservationByIdAccommodation(0L, VALID_RESERVATION_ID);

        assert(result.isEmpty());
    }

    @Test
    void testFindReservationByIdAccommodation_InvalidReservationId() {

        Reservation mockReservation = new Reservation();
        Mockito.when(reservationRepository.findByIdAndAccommodation(VALID_ACCOMMODATION_ID, VALID_RESERVATION_ID))
                .thenReturn(Optional.of(mockReservation));

        Optional<Reservation> result = reservationService.findReservationByIdAccommodation(VALID_ACCOMMODATION_ID, 0L);

        assert(result.isEmpty());
    }

    @Test
    void testAcceptRequest_Conflict() {
        var accommodation = Accommodation.builder().id(1L).build();

        var reservationToBeAccepted = Reservation.builder()
                .id(1L)
                .status(ReservationStatus.PENDING)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(3))
                .reservationEndDate(LocalDate.now().plusDays(12))
                .build();
        var reservationConflict = Reservation.builder()
                .id(2L)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(5))
                .reservationEndDate(LocalDate.now().plusDays(10))
                .status(ReservationStatus.PENDING)
                .build();




        List<Reservation> conflictedReservations = Arrays.asList(reservationConflict);

        Mockito.when(reservationRepository.findPendingConflictedReservations(
                        reservationToBeAccepted.getAccommodation().getId(),
                        reservationToBeAccepted.getId(),
                        reservationToBeAccepted.getReservationStartDate(),
                        reservationToBeAccepted.getReservationEndDate()))
                .thenReturn(conflictedReservations);

        reservationService.acceptRequest(reservationToBeAccepted);

        for (Reservation conflictReservation : conflictedReservations) {
            Mockito.verify(reservationRepository).save(conflictReservation);
            assert(conflictReservation.getStatus()).equals(ReservationStatus.DECLINED);
            assert(conflictReservation.getReason()).equals("Other reservation was accepted");
        }

        Mockito.verify(reservationRepository).save(reservationToBeAccepted);
        assert(reservationToBeAccepted.getStatus()).equals(ReservationStatus.ACCEPTED);
        assert(reservationToBeAccepted.getReason()).equals("ACCEPTED");
    }

    @Test
    void testAcceptRequest_NoConflict() {
        var accommodation = Accommodation.builder().id(1L).build();

        var reservationToBeAccepted = Reservation.builder()
                .id(1L)
                .status(ReservationStatus.PENDING)
                .accommodation(accommodation)
                .reservationStartDate(LocalDate.now().plusDays(3))
                .reservationEndDate(LocalDate.now().plusDays(12))
                .build();




        List<Reservation> conflictedReservations = Arrays.asList();

        Mockito.when(reservationRepository.findPendingConflictedReservations(
                        reservationToBeAccepted.getAccommodation().getId(),
                        reservationToBeAccepted.getId(),
                        reservationToBeAccepted.getReservationStartDate(),
                        reservationToBeAccepted.getReservationEndDate()))
                .thenReturn(conflictedReservations);

        reservationService.acceptRequest(reservationToBeAccepted);



        Mockito.verify(reservationRepository).save(reservationToBeAccepted);
        Mockito.verify(reservationRepository, Mockito.times(1)).save(Mockito.any(Reservation.class));
        assert(reservationToBeAccepted.getStatus()).equals(ReservationStatus.ACCEPTED);
        assert(reservationToBeAccepted.getReason()).equals("ACCEPTED");
    }


    
}
