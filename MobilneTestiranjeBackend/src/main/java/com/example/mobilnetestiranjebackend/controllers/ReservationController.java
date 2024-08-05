package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationSearchDTO;
import com.example.mobilnetestiranjebackend.DTOs.ReservationDTO;
import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.exceptions.*;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import com.example.mobilnetestiranjebackend.services.AvailabilityService;
import com.example.mobilnetestiranjebackend.services.ReservationService;
import com.fasterxml.jackson.databind.node.TextNode;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accommodations")
@RequiredArgsConstructor
public class ReservationController {


    private final AccommodationService accommodationService;
    private final AvailabilityService availabilityService;
    private final ReservationService reservationService;

    @PreAuthorize("hasAuthority('GUEST') or hasAuthority('OWNER')")
    @GetMapping("/{accommodationId}/reservations/{reservationId}")
    public ResponseEntity<?> getReservationDetails(@PathVariable("accommodationId") Long accommodationId,
                                                   @PathVariable("reservationId") Long reservationId,
                                                   @AuthenticationPrincipal User user){

        return ResponseEntity.ok().body(reservationService.getReservationDetails(accommodationId, reservationId, user.getId()));
    }

    @PreAuthorize("hasAuthority('GUEST')")
    @PostMapping("/{accommodationId}/reservation")
    public ResponseEntity<?> createReservation(@PathVariable("accommodationId") Long accommodationId,
                                               @RequestBody ReservationDTO request, @AuthenticationPrincipal Guest guest){

        Optional<Accommodation> accommodationWrapper = accommodationService.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");
        Accommodation accommodation = accommodationWrapper.get();
        if(request.getGuestNum() > accommodation.getMaxGuests() || request.getGuestNum() < accommodation.getMinGuests())
            throw new InvalidInputException("The minimum number of guests is " + accommodation.getMinGuests() + " and maximum is " + accommodation.getMaxGuests());

        Optional<Availability> availabilityWrapper = availabilityService.findAvailabilityForReservationRange(accommodationId, request.getReservationStartDate(), request.getReservationEndDate());

        if(availabilityWrapper.isEmpty())
            throw new NonExistingEntityException("Availability with this id for wanted accommodation doesn't exist");
        Availability avail = availabilityWrapper.get();


        var startDate = request.getReservationStartDate();
        var endDate = request.getReservationEndDate();
        var availStart = avail.getStartDate();
        var availEnd = avail.getEndDate();

//        if(!(startDate.isBefore(availEnd) && startDate.isAfter(availStart)))
//            throw new InvalidDateException("Start date is out of range for availability period");
//
//        if(!(endDate.isBefore(availEnd) && endDate.isAfter(availStart)))
//            throw new InvalidDateException("End date is out of range for availability period");


        if(reservationService.acceptedReservationRangeTaken(startDate, endDate, accommodation.getId(), avail.getId()))
            throw new InvalidDateException("There is already an accepted reservation for this date range");

        if(reservationService.acceptedReservationRangeTakenGuest(startDate, endDate, guest.getId()))
            throw new InvalidDateException("You already have an active reservation within this date range");


        reservationService.createNewReservation(request, guest, accommodation, avail);


        return ResponseEntity.ok().body("Successfully created new reservation request");
    }


    @PreAuthorize("hasAuthority('OWNER')")
    @PutMapping(value = "/{accommodationId}/reservations/{reservationId}/{status}")
    public ResponseEntity<?> processReservationRequest(@RequestBody(required = false) String reason,
                                                       @PathVariable("reservationId") Long reservationId,
                                                       @PathVariable("accommodationId") Long accommodationId,
                                                       @PathVariable("status") ReservationStatus status,
                                                       @AuthenticationPrincipal Owner owner){

        if(status.equals(ReservationStatus.DECLINED) && (reason == null || reason.equals(""))) throw new InvalidInputException("Reason must be provided");

        Optional<Accommodation> accommodationWrapper = accommodationService.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");
        Accommodation accommodation = accommodationWrapper.get();


        Optional<Reservation> reservationWrapper = reservationService.findReservationByIdAccommodation(accommodationId, reservationId);
        if(reservationWrapper.isEmpty()) throw new NonExistingEntityException("Reservation with this id doesn't exist");
        Reservation reservation = reservationWrapper.get();

        if(!accommodation.getOwner().getId().equals(owner.getId()))
            throw new InvalidAuthorizationException("You cannot do action for a accommodation you don't own");


        if(!reservation.getStatus().equals(ReservationStatus.PENDING))
            throw new InvalidEnumValueException("You can only do this action for a pending request reservation");

        String response = "";
        if(status.equals(ReservationStatus.DECLINED)){ response = "Successfully declined reservation"; reservationService.declineRequest(reason, reservation);}
        else if(status.equals(ReservationStatus.ACCEPTED)) { response = "Successfully accepted reservation"; reservationService.acceptRequest(reservation);}
        else throw new InvalidEnumValueException("Unsupported action");


        return ResponseEntity.ok().body(response);
    }


    @PreAuthorize("hasAuthority('GUEST')")
    @PutMapping(value = "/{accommodationId}/reservation/{reservationId}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable("accommodationId") Long accommodationId,
                                               @PathVariable("reservationId") Long reservationId,
                                               @AuthenticationPrincipal Guest guest){

        Optional<Accommodation> accommodationWrapper = accommodationService.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");

        Optional<Reservation> reservationWrapper = reservationService.findReservationByIdAccommodation(accommodationId, reservationId);
        if(reservationWrapper.isEmpty()) throw new NonExistingEntityException("Reservation with this id doesn't exist");
        Reservation reservation = reservationWrapper.get();

        if(!reservation.getStatus().equals(ReservationStatus.ACCEPTED))
            throw new InvalidEnumValueException("You can only cancel an accepted request reservation");

        if(!reservation.getGuest().getId().equals(guest.getId()))
            throw new InvalidAuthorizationException("You don't own this reservation");


        if(LocalDate.now().isBefore(reservation.getCancelDeadline()))
            reservationService.cancelReservation(reservation);
        else throw new InvalidDateException("The cancellation deadline has passed");


        return ResponseEntity.ok().body("Successfully canceled a reservation");
    }


    @PreAuthorize("hasAuthority('GUEST')")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> deletePendingReservationRequest(@AuthenticationPrincipal Guest guest,
                                                             @PathVariable("reservationId") Long reservationId){
        reservationService.deletePendingReservation(guest, reservationId);
        return ResponseEntity.ok().body("Pending request successfully deleted");
    }


    @PreAuthorize("hasAuthority('OWNER')")
    @GetMapping("/reservations")
    public ResponseEntity<?> getReservations(@RequestParam(required = false) String addressOrName,
                                                     @RequestParam(required = false) LocalDate minDate,
                                                     @RequestParam(required = false) LocalDate maxDate,
                                                     @RequestParam(required = false) ReservationStatus reservationStatus,
                                                     @RequestParam(defaultValue = "0") int pageNo,
                                                     @RequestParam(defaultValue = "10") int pageSize,
                                                     @AuthenticationPrincipal User user){

        Page<ReservationDTO> reservations = reservationService.getReservations(addressOrName, minDate, maxDate, reservationStatus, pageNo, pageSize, user.getId());

        return ResponseEntity.ok().body(reservations);
    }


    @PreAuthorize("hasAuthority('OWNER')")
    @GetMapping("/{accommodationId}/reservations/{reservationId}/conflict-reservations")
    public ResponseEntity<?> getConflictedReservations(@PathVariable("accommodationId") Long accommodationId,
                                                       @PathVariable("reservationId") Long reservationId,
                                                       @RequestParam(defaultValue = "0") int pageNo,
                                                       @RequestParam(defaultValue = "10") int pageSize,
                                                       @AuthenticationPrincipal Owner owner){

        Page<ReservationDTO> conflictReservations = reservationService.getConflictReservations(accommodationId, reservationId, owner, pageNo, pageSize);
        return ResponseEntity.ok().body(conflictReservations);
    }










}
