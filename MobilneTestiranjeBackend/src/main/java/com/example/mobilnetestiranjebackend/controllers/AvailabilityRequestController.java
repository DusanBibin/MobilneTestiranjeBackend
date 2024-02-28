package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.DTOs.AvailabilityDTO;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.exceptions.InvalidEnumValueException;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import com.example.mobilnetestiranjebackend.services.AvailabilityRequestService;
import com.example.mobilnetestiranjebackend.services.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accommodations/{accommodationId}/availability-request")
public class AvailabilityRequestController {


    private final AvailabilityRequestService availabilityRequestService;
    @PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(value = "/create-new-request")
    public ResponseEntity<?> createAvailabilityRequest(@Valid @RequestBody AvailabilityDTO availabilityDTO,
                                                       @PathVariable("accommodationId") Long accommodationId,
                                                       @AuthenticationPrincipal Owner owner) {

        availabilityRequestService.createAvailabilityRequest(availabilityDTO, accommodationId, owner);

        return ResponseEntity.ok().body("Successfully created new availability  request");
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(value = "/create-edit-request/availability/{availId}")
    public ResponseEntity<?> createEditAvailabilityRequest(@Valid @RequestBody AvailabilityDTO availabilityDTO,
                                                       @PathVariable("accommodationId") Long accommodationId,
                                                       @PathVariable("availId") Long availId,
                                                       @AuthenticationPrincipal Owner owner) {

        availabilityRequestService.createEditAvailabilityRequest(availabilityDTO, accommodationId, owner, availId);

        return ResponseEntity.ok().body("Successfully created new edit availability request");
    }

    @PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(value = "/create-delete-request/availability/{availId}")
    public ResponseEntity<?> createDeleteAvailabilityRequest(@PathVariable("accommodationId") Long accommId,
                                                           @PathVariable("availId") Long availId,
                                                           @AuthenticationPrincipal Owner owner) {

        availabilityRequestService.createDeleteAvailabilityRequest(accommId, owner, availId);

        return ResponseEntity.ok().body("Successfully created new delete availability request");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{requestId}/{status}")
    public ResponseEntity<?> acceptAvailabilityRequest(@PathVariable("accommodationId") Long accommodationId,
                                                       @PathVariable("requestId") Long requestId,
                                                       @PathVariable("status") RequestStatus status,
                                                       @RequestBody(required = false) String reason){

        if(status.equals(RequestStatus.ACCEPTED)) availabilityRequestService.acceptRequest(requestId, accommodationId);
        else if(status.equals(RequestStatus.REJECTED)) availabilityRequestService.declineRequest(reason, requestId, accommodationId);
        else throw new InvalidEnumValueException("Invalid operation");

        return ResponseEntity.ok().body("Successfully accepted availability request");
    }



}
