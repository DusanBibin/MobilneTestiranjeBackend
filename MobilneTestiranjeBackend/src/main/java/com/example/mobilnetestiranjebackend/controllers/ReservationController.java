package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.ReservationDTO;
import com.example.mobilnetestiranjebackend.exceptions.InvalidDateException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.Accommodation;
import com.example.mobilnetestiranjebackend.model.AccommodationAvailability;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.User;
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




        //PROVERA DA LI SE RANGE NALAZI U ACCAVAILABILITY
        var startDate = request.getReservationStartDate();
        var endDate = request.getReservationStartDate();
        var availStart = avail.getStartDate();
        var availEnd = avail.getEndDate();


        if(!(startDate.isBefore(availEnd) && startDate.isAfter(availStart)))
            throw new InvalidDateException("Start date is out of range for availability period");

        if(!(endDate.isBefore(availEnd) && endDate.isAfter(availStart)))
            throw new InvalidDateException("End date is out of range for availability period");




        //PROVERA DA LI POSTOJI VEC REZERVACIJA KOJA JE PRIHVACNEA ZA TAJ PERIOD
        if(reservationService.acceptedReservationRangeTaken(startDate, endDate, accom.getId(), avail.getId()))
            throw new InvalidDateException("There is already an accepted reservation for this date range");



        //NAPRAVITI NOVI RESERVATION SA ACCEPTED ILI PENDING U ZAVISNOSTI DA LI JE AUTOACCEPTENABLED

        reservationService.createNewReservation(request, guest, accom, avail);


        //SACUVATI REZERVACIJU


        return ResponseEntity.ok().body("Successfully created new reservation request");

    }
}
