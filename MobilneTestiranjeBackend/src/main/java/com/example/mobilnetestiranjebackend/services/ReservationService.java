package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.ReservationDTO;
import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.exceptions.InvalidEnumValueException;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Availability;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;


    public void acceptRequest(Reservation reservation) {
        List<Reservation> conflictedReservations = reservationRepository
                .findPendingConflictedReservations(reservation.getAccommodation().getId(), reservation.getId(),
                        reservation.getReservationStartDate(), reservation.getReservationEndDate());

        for(Reservation conflictReservation: conflictedReservations){
            conflictReservation.setReason("Other reservation was accepted");
            conflictReservation.setStatus(ReservationStatus.DECLINED);
            reservationRepository.save(conflictReservation);
        }

        reservation.setReason("ACCEPTED");
        reservation.setStatus(ReservationStatus.ACCEPTED);
        reservationRepository.save(reservation);
    }



    public Optional<Reservation> findReservationByIdAccommodation(Long accommodationId, Long reservationId) {
        return reservationRepository.findByIdAndAccommodation(accommodationId, reservationId);
    }


    public boolean acceptedReservationRangeTaken(LocalDate startDate, LocalDate endDate, Long accomId, Long availId) {
        List<Reservation> sameRangeReservations = reservationRepository.
                 findAcceptedReservationsInConflict(startDate, endDate, accomId, availId);

        return !sameRangeReservations.isEmpty();
    }


    public void createNewReservation(ReservationDTO request, Guest guest, Accommodation accom, Availability avail) {

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
                .availability(avail)
                .build();

        reservationRepository.save(reservation);

        accom.getReservations().add(reservation);
        accommodationRepository.save(accom);
    }

    public Optional<Reservation> findReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    public Optional<Reservation> findReservationByIdAndGuest(Long reservationId, Long guestId){
        return reservationRepository.findByIdAndGuest(reservationId, guestId);
    }

    public void declineRequest(String reason, Reservation reservation) {

        if(!reservation.getStatus().equals(ReservationStatus.PENDING)) throw new InvalidEnumValueException("You can only decline a pending request");

        reservation.setStatus(ReservationStatus.DECLINED);
        reservation.setReason(reason);

        reservationRepository.save(reservation);
    }



    public void cancelReservation(Reservation reservation) {

        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);
    }


}
