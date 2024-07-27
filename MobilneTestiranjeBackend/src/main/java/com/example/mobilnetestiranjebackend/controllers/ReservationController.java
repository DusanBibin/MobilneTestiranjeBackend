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
@RequestMapping("/api/v1/accommodations/{accommodationId}")
@RequiredArgsConstructor
public class ReservationController {


    private final AccommodationService accommodationService;
    private final AvailabilityService availabilityService;
    private final ReservationService reservationService;



    @PreAuthorize("hasAuthority('GUEST')")
    @PostMapping("/reservation")
    public ResponseEntity<?> createReservation(@PathVariable("accommodationId") Long accommodationId,
                                               @RequestBody ReservationDTO request, @AuthenticationPrincipal Guest guest){

        Optional<Accommodation> accommodationWrapper = accommodationService.findAccommodationById(accommodationId);
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");
        Accommodation accom = accommodationWrapper.get();


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


        if(reservationService.acceptedReservationRangeTaken(startDate, endDate, accom.getId(), avail.getId()))
            throw new InvalidDateException("There is already an accepted reservation for this date range");

        if(reservationService.acceptedReservationRangeTakenGuest(startDate, endDate, guest.getId()))
            throw new InvalidDateException("You already have an active reservation within this date range");


        reservationService.createNewReservation(request, guest, accom, avail);


        return ResponseEntity.ok().body("Successfully created new reservation request");
    }


    @PreAuthorize("hasAuthority('OWNER')")
    @PutMapping(value = "/reservations/{reservationId}/{status}")
    public ResponseEntity<?> declineReservationRequest(@RequestBody TextNode reason,
                                                       @PathVariable("reservationId") Long reservationId,
                                                       @PathVariable("accommodationId") Long accommodationId,
                                                       @PathVariable("status") ReservationStatus status,
                                                       @AuthenticationPrincipal Owner owner){


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

        if(status.equals(ReservationStatus.DECLINED)) reservationService.declineRequest(reason.asText(), reservation);
        else if(status.equals(ReservationStatus.ACCEPTED))reservationService.acceptRequest(reservation);
        else throw new InvalidEnumValueException("Unsupported action");


        return ResponseEntity.ok().body("Successfully declined a reservation request");
    }


    @PreAuthorize("hasAuthority('GUEST')")
    @PutMapping(value = "/reservations/{reservationId}/cancel")
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


        if(LocalDate.now().isBefore(reservation.getAvailability().getCancelDeadline()))
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


    @PreAuthorize("hasAuthority('GUEST') or hasAuthority('OWNER')")
    @GetMapping("/reservation")
    public ResponseEntity<?> getReservations(@RequestParam(defaultValue = "1") Long guestNum, @RequestParam @NotBlank(message = "Address must be present") String address,
                                                     @RequestParam @FutureOrPresent(message = "Start date must be in the future") LocalDate startDate,
                                                     @RequestParam @FutureOrPresent(message = "End date must be in the future") LocalDate endDate,
                                                     @RequestParam(required = false) List<Amenity> amenities,
                                                     @RequestParam(required = false) AccommodationType accommodationType,
                                                     @RequestParam(required = false) Long minPrice,
                                                     @RequestParam(required = false) Long maxPrice,
                                                     @RequestParam(defaultValue = "price") String sortType,
                                                     @RequestParam(defaultValue = "true") boolean isAscending,
                                                     @RequestParam(defaultValue = "0") int pageNo,
                                                     @RequestParam(defaultValue = "10") int pageSize){


        return ResponseEntity.ok().body("kurac");
    }









}
