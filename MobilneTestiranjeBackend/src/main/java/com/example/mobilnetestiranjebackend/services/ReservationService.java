package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.ReservationDTO;
import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.AccommodationAvailability;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;
    public boolean acceptedReservationRangeTaken(LocalDate startDate, LocalDate endDate, Long accomId, Long availId) {
        List<Reservation> sameRangeReservations = reservationRepository.
                findAcceptedReservationsInConflict(startDate, endDate, accomId, availId);

        return sameRangeReservations.isEmpty();
    }


    public void createNewReservation(ReservationDTO request, Guest guest, Accommodation accom, AccommodationAvailability avail) {

        ReservationStatus status = ReservationStatus.PENDING;
        String reason = "";
        if(accom.getAutoAcceptEnabled()){
            status = ReservationStatus.ACCEPTED;
            reason = "ACCEPTED";
        }

        var reservation = Reservation.builder()
                .reservationStartDate(request.getReservationStartDate())
                .reservationEndDate(request.getReservationEndDate())
                .guestNum(request.getGuestNum())
                .status(status)
                .reason(reason)
                .guest(guest)
                .accommodation(accom)
                .accommodationAvailability(avail)
                .build();

        reservationRepository.save(reservation);

        accom.getReservations().add(reservation);
        accommodationRepository.save(accom);


    }
}
