package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accommodation")
@RequiredArgsConstructor
public class AccommodationController {

    //@PreAuthorize("hasAuthority('OWNER')")

    //SWAGGER NE RADI ZA OVAJ ENDPOINT, KORISTITI POSTMAN
    @PostMapping(path = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createAccommodation(@Valid @RequestPart("accommodationDTO") AccommodationDTO accommodationDTO,
                                                 @Valid @NotEmpty(message = "There must be at least one image") @RequestPart("photos") List<MultipartFile> images)
    {
        System.out.println(images.size());
        System.out.println(accommodationDTO.getName());
        return ResponseEntity.ok().body("Hello from endpoint");
    }


}
