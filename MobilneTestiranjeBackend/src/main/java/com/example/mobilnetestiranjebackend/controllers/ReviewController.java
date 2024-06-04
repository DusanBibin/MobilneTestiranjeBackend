package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.AccommodationSearchDTO;
import com.example.mobilnetestiranjebackend.DTOs.ReviewDTO;
import com.example.mobilnetestiranjebackend.DTOs.ReviewDTOResponse;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.repositories.AccommodationReviewRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerRepository;
import com.example.mobilnetestiranjebackend.repositories.OwnerReviewRepository;
import com.example.mobilnetestiranjebackend.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final AccommodationReviewRepository accommodationReviewRepository;
    private final OwnerRepository ownerRepository;
    private final OwnerReviewRepository ownerReviewRepository;

    @PreAuthorize("hasAuthority('GUEST')")
    @PostMapping("/owners/{ownerId}")
    public ResponseEntity<?> createOwnerReview(@PathVariable("ownerId") Long ownerId,
                                               @RequestBody ReviewDTO reviewDTO,
                                               @AuthenticationPrincipal Guest guest){
        reviewService.createOwnerReview(reviewDTO, ownerId, guest.getId());

        return ResponseEntity.ok().body("Successfully created new owner review");
    }


    @PreAuthorize("hasAuthority('GUEST')")
    @DeleteMapping("/owner-reviews/{reviewId}")
    public ResponseEntity<?> deleteOwnerReview(@PathVariable("reviewId") Long reviewId,
                                               @AuthenticationPrincipal Guest guest){
        reviewService.deleteOwnerReview(reviewId, guest.getId());
        return ResponseEntity.ok().body("Successfully deleted owner review");
    }



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
    @DeleteMapping("/accommodation-reviews/{reviewId}")
    public ResponseEntity<?> deleteAccommodationReview(@PathVariable("reviewId") Long reviewId,
                                                       @AuthenticationPrincipal Guest guest) {
        reviewService.deleteAccommodationReview(reviewId, guest.getId());
        return ResponseEntity.ok().body("Successfully deleted owner review");
    }


    @GetMapping("/accommodations/{accommodationId}")
    public ResponseEntity<?> getAccommodationReviews(@PathVariable("accommodationId") Long accommodationId,
                                                     @RequestParam(defaultValue = "0") int pageNo,
                                                     @RequestParam(defaultValue = "10") int pageSize){

        Page<ReviewDTOResponse> pagedReviews = reviewService.getAccommodationReviews(accommodationId, pageNo, pageSize);
        return ResponseEntity.ok().body(pagedReviews);
    }

}
