package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AccommodationDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner")
public class OwnerController {


    @PostMapping("/accommodation")
    public ResponseEntity<?> createAccommodation(@Valid @RequestBody AccommodationDTO accommodationDTO)
    {
        return ResponseEntity.ok("Hello from secured endpoint");
    }
}
