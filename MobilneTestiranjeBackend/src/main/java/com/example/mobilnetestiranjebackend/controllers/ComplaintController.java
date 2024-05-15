package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.exceptions.InvalidEnumValueException;
import com.example.mobilnetestiranjebackend.model.Admin;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.ComplaintService;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PreAuthorize("hasAuthority('GUEST') or hasAuthority('OWNER')")
    @PostMapping("/users/{reportedId}")
    public ResponseEntity<?> createUserComplaint(@PathVariable("reportedId") Long reportedId,
                                                 @AuthenticationPrincipal User user,
                                                 @RequestBody TextNode reason){

        complaintService.createUserComplaint(user.getId(), reportedId, reason.asText());

        return new ResponseEntity<>(("Successfully created new review complaint"), HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('OWNER')")
    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<?> createReviewComplaint(@PathVariable("reviewId") Long reviewId,
                                                   @AuthenticationPrincipal Owner owner,
                                                   @RequestBody TextNode reason){

        complaintService.createReviewComplaint(owner.getId(), reviewId, reason.asText());

        return new ResponseEntity<>(("Successfully created new review complaint"), HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/review-complaint/{complaintId}/{status}")
    public ResponseEntity<?> reviewReviewComplaint(@PathVariable("complaintId") Long complaintId,
                                             @PathVariable("status") RequestStatus status,
                                                   @AuthenticationPrincipal Admin admin,
                                                   @RequestBody TextNode response){

        if(!status.equals(RequestStatus.ACCEPTED) && !status.equals(RequestStatus.REJECTED)) throw new InvalidEnumValueException("Invalid operation");

        complaintService.reviewReviewComplaint(complaintId, response.asText(), status);

        return new ResponseEntity<>(("Successfully reviewed a review complaint"), HttpStatus.OK);

    }



    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/user-complaint/{complaintId}/{status}")
    public ResponseEntity<?> reviewUserComplaint(@PathVariable("complaintId") Long complaintId,
                                                   @PathVariable("status") RequestStatus status,
                                                   @AuthenticationPrincipal Admin admin,
                                                   @RequestBody TextNode response){

        if(!status.equals(RequestStatus.ACCEPTED) && !status.equals(RequestStatus.REJECTED)) throw new InvalidEnumValueException("Invalid operation");

        complaintService.reviewUserComplaint(complaintId, response.asText(), status);

        return new ResponseEntity<>(("Successfully reviewed a review complaint"), HttpStatus.OK);

    }



}
