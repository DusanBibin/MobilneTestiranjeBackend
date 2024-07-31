package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationRequestPreviewDTO;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationSearchDTO;
import com.example.mobilnetestiranjebackend.DTOs.ReservationDTO;
import com.example.mobilnetestiranjebackend.enums.RequestType;
import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidEnumValueException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.helpers.PageConverter;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.Availability;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.Reservation;
import com.example.mobilnetestiranjebackend.repositories.AccommodationRepository;
import com.example.mobilnetestiranjebackend.repositories.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final AccommodationRepository accommodationRepository;






    public Optional<Reservation> findReservationByIdAccommodation(Long accommodationId, Long reservationId) {
        return reservationRepository.findByIdAndAccommodation(accommodationId, reservationId);
    }


    public boolean acceptedReservationRangeTaken(LocalDate startDate, LocalDate endDate, Long accomId, Long availId) {
        List<Reservation> sameRangeReservations = reservationRepository.
                 findAcceptedReservationsInConflict(startDate, endDate, accomId, availId);

        return !sameRangeReservations.isEmpty();
    }

    public boolean acceptedReservationRangeTakenGuest(LocalDate startDate, LocalDate endDate, Long guestId) {
        List<Reservation> sameRangeReservations = reservationRepository.
                findGuestReservationsInConflict(startDate, endDate, guestId);

        return !sameRangeReservations.isEmpty();
    }

    public void createNewReservation(ReservationDTO request, Guest guest, Accommodation accom, Availability avail) {

        ReservationStatus status = ReservationStatus.PENDING;
        String reason = "";
        if(accom.getAutoAcceptEnabled()){
            status = ReservationStatus.ACCEPTED;
            reason = "ACCEPTED";
        }


        Long days = ChronoUnit.DAYS.between(request.getReservationStartDate(), request.getReservationEndDate()) + 1;

        Long totalPrice;
        if (avail.getPricePerGuest()) totalPrice = days * avail.getPrice() * request.getGuestNum();
        else totalPrice = days * avail.getPrice();

        var reservation = Reservation.builder()
                .reservationStartDate(request.getReservationStartDate())
                .reservationEndDate(request.getReservationEndDate())
                .guestNum(request.getGuestNum())
                .status(status)
                .reason(reason)
                .guest(guest)
                .accommodation(accom)
                .availability(avail)
                .price(totalPrice)
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

    public void cancelReservation(Reservation reservation) {

        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);
    }



    public Boolean reservationsNotEnded(Long accommodationId) {
        List<Reservation> reservationsNotEnded = reservationRepository.findReservationsNotEndedByAccommodationId(accommodationId);
        return !reservationsNotEnded.isEmpty();
    }

    public Boolean reservationsNotEnded(Long accommodationId, Long availabilityId) {
        List<Reservation> reservationsNotEnded = reservationRepository
                .findReservationNotEndedByAvailabilityIdAndAccommodationId(accommodationId, availabilityId);
        return !reservationsNotEnded.isEmpty();
    }

    public void deletePendingReservation(Guest guest, Long reservationId) {
        var reservationWrapper = reservationRepository.findById(reservationId);
        if(reservationWrapper.isEmpty()) throw new NonExistingEntityException("Reservation with this id doesn't exist");

        reservationWrapper = reservationRepository.findByIdAndGuest(reservationId, guest.getId());
        if(reservationWrapper.isEmpty()) throw new InvalidAuthorizationException("You do not own this reservation");
        var reservation = reservationWrapper.get();

        if(!reservation.getStatus().equals(ReservationStatus.PENDING)) throw new InvalidAuthorizationException("You cannot delete non pending reservation");
        reservationRepository.delete(reservation);
    }

    public Page<ReservationDTO> getReservations(String addressOrName, LocalDate minDate, LocalDate maxDate, ReservationStatus reservationStatus, int pageNo, int pageSize, Long ownerId) {

        List<Reservation> reservations = reservationRepository.findHostReservations(minDate, maxDate, reservationStatus,addressOrName, ownerId);
        System.out.println(reservations.size());
        List<ReservationDTO> reservationsDTO = new ArrayList<>(reservations.stream().map(a -> {
            ReservationDTO r = new ReservationDTO();
            r.setPrice(a.getPrice());
            r.setStatus(a.getStatus());
            r.setReason(a.getReason());
            r.setReservationEndDate(a.getReservationEndDate());
            r.setReservationStartDate(a.getReservationStartDate());
            r.setAccommodationName(a.getAccommodation().getName());
            return r;
        }).toList());

        return PageConverter.convertListToPage(pageNo, pageSize, reservationsDTO);
    }

}
