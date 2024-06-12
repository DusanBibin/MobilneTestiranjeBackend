package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTOEdit;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.exceptions.InvalidEnumValueException;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.AccommodationRequestService;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accommodation-requests")
@RequiredArgsConstructor
public class AccommodationRequestController {
    private final AccommodationService accommodationService;
    private final AccommodationRequestService accommodationRequestService;

    @PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createAccommodationRequest(@Valid @RequestPart("accommodationDTO") AccommodationDTO accommodationDTO,
                                                        @RequestPart("images") List<MultipartFile> images,
                                                        @AuthenticationPrincipal Owner owner) {
        accommodationRequestService.createAccommodationRequest(owner, images, accommodationDTO);
        return ResponseEntity.ok().body("Successfully created new accommodation request");
    }


    @PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(path = "/accommodations/{accommodationId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createEditAccommodationRequest(@Valid @RequestPart("accommodationDTO") AccommodationDTOEdit accommodationDTO,
                                                            @RequestPart("photos") List<MultipartFile> images,
                                                            @AuthenticationPrincipal Owner owner,
                                                            @PathVariable("accommodationId") Long accommodationId) {

        accommodationRequestService.createEditAccommodationRequest(owner, images, accommodationDTO, accommodationId);
        return ResponseEntity.ok().body("Successfully created new accommodation request");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(path = "/{requestId}/{status}")
    public ResponseEntity<?> processAccommodationRequest(@PathVariable("requestId") Long requestId,
                                                        @PathVariable("status") RequestStatus status,
                                                        @RequestBody(required = false) String reason){

        if(status.equals(RequestStatus.ACCEPTED)) accommodationRequestService.acceptRequest(requestId);
        else if(status.equals(RequestStatus.REJECTED)) accommodationRequestService.declineRequest(requestId, reason);
        else throw new InvalidEnumValueException("Invalid status value");

        return ResponseEntity.ok().body("Successfully accepted accommodation request");
    }

}
