package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationAvailabilityDTO;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accommodation/{accommodationId}/availability")
@RequiredArgsConstructor
public class AvailabilityController {
    private final AvailabilityService availabilityService;
    //@PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(path = "/create")
    public ResponseEntity<?> createAvailability(@Valid @RequestBody AccommodationAvailabilityDTO availabilityDTO,
                                                @PathVariable Long accommodationId,
                                                 @AuthenticationPrincipal User user) {

        availabilityService.createAvailability(user.getId(), availabilityDTO, accommodationId);

        return ResponseEntity.ok().body("Successfully created new request for availability period");
    }
}
