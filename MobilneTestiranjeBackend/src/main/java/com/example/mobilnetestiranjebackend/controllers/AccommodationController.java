package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.AccommodationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accommodation")
@RequiredArgsConstructor
public class AccommodationController {
    private final AccommodationService accommodationService;
    //@PreAuthorize("hasAuthority('OWNER')")
    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createAccommodationRequest(@Valid @RequestPart("accommodationDTO") AccommodationDTO accommodationDTO,
                                                 @RequestPart("photos") List<MultipartFile> images,
                                                 @AuthenticationPrincipal User user) {

        accommodationService.createAccommodationRequest(user.getId(), images, accommodationDTO);

        return ResponseEntity.ok().body("Successfully created new accommodation request");
    }
}
