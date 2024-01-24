package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
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

import java.util.ArrayList;
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
                                                        @AuthenticationPrincipal User user) {
        accommodationRequestService.createAccommodationRequest(user.getId(), images, accommodationDTO);
        return ResponseEntity.ok().body("Successfully created new accommodation request");
    }

    //@PreAuthorize("hasAuthority('OWNER')")
    //@RequestPart("photos") List<MultipartFile> images
    //, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    //@RequestPart("accommodationDTO") AccommodationDTO accommodationDTO
    @PostMapping(path = "/edit/accommodation/{accommodationId}")
    public ResponseEntity<?> createEditAccommodationRequest(@Valid @RequestBody AccommodationDTO accommodationDTO,
                                                            @AuthenticationPrincipal User user,
                                                            @PathVariable("accommodationId") Long accommdationId) {

        List<MultipartFile> images = new ArrayList<>();
        accommodationRequestService.createEditAccommodationRequest(user.getId(), accommodationDTO, accommdationId);
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
