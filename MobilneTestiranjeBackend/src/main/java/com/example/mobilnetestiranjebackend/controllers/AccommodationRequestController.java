package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.AccommodationRequestService;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accommodation-request")
@RequiredArgsConstructor
public class AccommodationRequestController {
    private final AccommodationService accommodationService;
    private final AccommodationRequestService accommodationRequestService;

    //@PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createAccommodationRequest(@Valid @RequestPart("accommodationDTO") AccommodationDTO accommodationDTO,
                                                        @RequestPart("photos") List<MultipartFile> images,
                                                        @AuthenticationPrincipal Owner owner) {
        accommodationRequestService.createAccommodationRequest(owner, images, accommodationDTO);
        return ResponseEntity.ok().body("Successfully created new accommodation request");
    }

    //@PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(path = "/edit/accommodation/{accommodationId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createEditAccommodationRequest(@Valid @RequestPart("accommodationDTO") AccommodationDTO accommodationDTO,
                                                            @RequestPart("photos") List<MultipartFile> images,
                                                            @AuthenticationPrincipal Owner owner,
                                                            @PathVariable("accommodationId") Long accommdationId) {

        accommodationRequestService.createEditAccommodationRequest(owner, images, accommodationDTO, accommdationId);
        return ResponseEntity.ok().body("Successfully created new accommodation request");
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(path = "/accept/{requestId}")
    public ResponseEntity<?> acceptAccommodationRequest(@PathVariable("requestId") Long requestId){

        accommodationRequestService.acceptRequest(requestId);

        return ResponseEntity.ok().body("Successfully accepted accommodation request");
    }

    //@PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(path = "/reject/{requestId}")
    public ResponseEntity<?> rejectAccommodationRequest(@PathVariable("requestId") Long requestId, @RequestBody String reason){

        accommodationRequestService.declineRequest(requestId, reason);
        return ResponseEntity.ok().body("Successfully rejected accommodation request");
    }

}
