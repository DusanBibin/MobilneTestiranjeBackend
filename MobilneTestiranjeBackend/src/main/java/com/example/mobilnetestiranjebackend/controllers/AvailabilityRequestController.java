package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.DTOs.AvailabilityDTO;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import com.example.mobilnetestiranjebackend.services.AvailabilityRequestService;
import com.example.mobilnetestiranjebackend.services.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/availability-request")
public class AvailabilityRequestController {


    private final AvailabilityRequestService availabilityRequestService;
    //@PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(value = "/accommodation/{accommId}/create")
    public ResponseEntity<?> createAvailabilityRequest(@Valid @RequestBody AvailabilityDTO availabilityDTO,
                                                       @PathVariable("accommId") Long accommId,
                                                       @AuthenticationPrincipal Owner owner) {

        availabilityRequestService.createAvailabilityRequest(availabilityDTO, accommId, owner);

        return ResponseEntity.ok().body("Successfully created new availability  request");
    }
    //@PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(value = "/accommodation/{accommId}/edit/availability/{availId}")
    public ResponseEntity<?> createEditAvailabilityRequest(@Valid @RequestBody AvailabilityDTO availabilityDTO,
                                                       @PathVariable("accommId") Long accommId,
                                                       @PathVariable("availId") Long availId,
                                                       @AuthenticationPrincipal Owner owner) {

        availabilityRequestService.createEditAvailabilityRequest(availabilityDTO, accommId, owner, availId);

        return ResponseEntity.ok().body("Successfully created new edit availability request");
    }

    //@PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(value = "/accommodation/{accommId}/delete/availability/{availId}")
    public ResponseEntity<?> createDeleteAvailabilityRequest(@PathVariable("accommId") Long accommId,
                                                           @PathVariable("availId") Long availId,
                                                           @AuthenticationPrincipal Owner owner) {

        availabilityRequestService.createDeleteAvailabilityRequest(accommId, owner, availId);

        return ResponseEntity.ok().body("Successfully created new delete availability request");
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/accept/{requestId}")
    public ResponseEntity<?> acceptAvailabilityRequest(@PathVariable("requestId") Long requestId) {

        availabilityRequestService.acceptRequest(requestId);

        return ResponseEntity.ok().body("Successfully accepted availability request");
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/decline/{requestId}")
    public ResponseEntity<?> declineAvailabilityRequest(@PathVariable("requestId") Long requestId, @RequestBody String reason) {


        availabilityRequestService.declineRequest(reason, requestId);
        return ResponseEntity.ok().body("Successfully rejected availability request");
    }

}
