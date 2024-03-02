package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.ReviewDTO;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.services.OwnerService;
import com.example.mobilnetestiranjebackend.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accommodation-reviews")
@RequiredArgsConstructor
public class AccommodationReviewController {

    private final ReviewService reviewService;

    @PreAuthorize("hasAuthority('GUEST')")
    @PostMapping("/accommodations/{accommodationId}/reservation/{reservationId}")
    public ResponseEntity<?> createAccommodationReview(@PathVariable("accommodationId") Long accommodationId,
                                                       @PathVariable("reservationId") Long reservationId,
                                                       @RequestBody ReviewDTO reviewDTO,
                                                       @AuthenticationPrincipal Guest guest){
        reviewService.createAccommodationReview(reviewDTO, accommodationId,reservationId, guest.getId());

        return ResponseEntity.ok().body("Successfully created new accommodation review");
    }

    @PreAuthorize("hasAuthority('GUEST')")
    @DeleteMapping("{reviewId}")
    public ResponseEntity<?> deleteAccommodationReview(@PathVariable("reviewId") Long reviewId,
                                               @AuthenticationPrincipal Guest guest) {
        reviewService.deleteAccommodationReview(reviewId, guest.getId());
        return ResponseEntity.ok().body("Successfully deleted owner review");
    }
}
