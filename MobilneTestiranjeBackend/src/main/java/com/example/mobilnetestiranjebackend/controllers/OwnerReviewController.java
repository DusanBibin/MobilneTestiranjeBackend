package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.ReviewDTO;
import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.services.OwnerReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner-reviews")
@RequiredArgsConstructor
public class OwnerReviewController {

    private final OwnerReviewService ownerService;

    @PreAuthorize("hasAuthority('GUEST')")
    @PostMapping("/owners/{ownerId}")
    public ResponseEntity<?> createOwnerReview(@PathVariable("ownerId") Long ownerId,
                                                       @RequestBody ReviewDTO reviewDTO,
                                                       @AuthenticationPrincipal Guest guest){
        ownerService.createOwnerReview(reviewDTO, ownerId, guest.getId());

        return ResponseEntity.ok().body("Successfully created new owner review");
    }

    @PreAuthorize("hasAuthority('GUEST')")
    @DeleteMapping("{reviewId}")
    public ResponseEntity<?> deleteOwnerReview(@PathVariable("reviewId") Long reviewId,
                                                       @AuthenticationPrincipal Guest guest){
        ownerService.deleteOwnerReview(reviewId, guest.getId());
        return ResponseEntity.ok().body("Successfully deleted owner review");
    }



}
