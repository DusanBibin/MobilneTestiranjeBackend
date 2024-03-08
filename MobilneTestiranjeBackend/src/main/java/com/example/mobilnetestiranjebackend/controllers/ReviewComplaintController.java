package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.services.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/review-complaints")
@RequiredArgsConstructor
public class ReviewComplaintController {

    private final ComplaintService complaintService;

    @PreAuthorize("hasAuthority('OWNER')")
    @PostMapping("/review/{reviewId}/")
    public ResponseEntity<?> createReviewComplaint(@PathVariable("reviewId") Long reviewId,
                                                   @AuthenticationPrincipal Owner owner,
                                                   @RequestBody String reason){

        complaintService.createReviewComplaint(owner.getId(), reviewId, reason);

        return new ResponseEntity<>(("Successfully created new review complaint"), HttpStatus.OK);

    }



}
