package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.ReservationDTO;
import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import com.example.mobilnetestiranjebackend.exceptions.InvalidDateException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.*;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import com.example.mobilnetestiranjebackend.services.AvailabilityService;
import com.example.mobilnetestiranjebackend.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/reservation/")
@RequiredArgsConstructor
public class ReservationController {


    private AccommodationService accommodationService;
    private AvailabilityService availabilityService;
    private ReservationService reservationService;

    @Autowired
    public ReservationController(AccommodationService accommodationService, AvailabilityService availabilityService, ReservationService reservationService) {
        this.accommodationService = accommodationService;
        this.availabilityService = availabilityService;
        this.reservationService = reservationService;
    }

    //@PreAuthorize("hasAuthority('GUEST')")
    @PostMapping(value = "create")
    public ResponseEntity<?> createReservation(@RequestBody ReservationDTO request, @AuthenticationPrincipal Guest guest){

        Optional<Accommodation> accommodationWrapper = accommodationService.findAccommodationById(request.getAccommodationId());
        if(accommodationWrapper.isEmpty()) throw new NonExistingEntityException("Accommodation with this id doesn't exist");
        Accommodation accom = accommodationWrapper.get();

        Optional<AccommodationAvailability> availabilityWrapper = availabilityService
                .findAvailabilityByIdAndAccommodation(request.getAvailabilityId(), request.getAccommodationId());

        if(availabilityWrapper.isEmpty())
            throw new NonExistingEntityException("Availability with this id for wanted accommodation doesn't exist");
        AccommodationAvailability avail = availabilityWrapper.get();

        var startDate = request.getReservationStartDate();
        var endDate = request.getReservationStartDate();
        var availStart = avail.getStartDate();
        var availEnd = avail.getEndDate();

        if(!(startDate.isBefore(availEnd) && startDate.isAfter(availStart)))
            throw new InvalidDateException("Start date is out of range for availability period");

        if(!(endDate.isBefore(availEnd) && endDate.isAfter(availStart)))
            throw new InvalidDateException("End date is out of range for availability period");


        if(reservationService.acceptedReservationRangeTaken(startDate, endDate, accom.getId(), avail.getId()))
            throw new InvalidDateException("There is already an accepted reservation for this date range");


        reservationService.createNewReservation(request, guest, accom, avail);


        return ResponseEntity.ok().body("Successfully created new reservation request");
    }


    @PutMapping(value = "/{reservationId}/decline")
    public ResponseEntity<?> declineReservationRequest(@RequestBody String reason, @PathVariable("reservationId") Long reservationId){


        Optional<Reservation> reservationWrapper = reservationService.findReservationById(reservationId);
        if(reservationWrapper.isEmpty()) throw new NonExistingEntityException("Reservation with this id doesn't exist");
        Reservation reservation = reservationWrapper.get();


        reservationService.declineRequest(reason, reservation);


        return ResponseEntity.ok().body("Successfully declined a reservation request");
    }


    @PutMapping(value = "/{reservationId}/decline")
    public ResponseEntity<?> acceptReservationRequest(@PathVariable("reservationId") Long reservationId){

        Optional<Reservation> reservationWrapper = reservationService.findReservationById(reservationId);
        if(reservationWrapper.isEmpty()) throw new NonExistingEntityException("Reservation with this id doesn't exist");
        Reservation reservation = reservationWrapper.get();


        reservationService.acceptRequest(reservation);


        return ResponseEntity.ok().body("Successfully accepted a reservation request");
    }
}
