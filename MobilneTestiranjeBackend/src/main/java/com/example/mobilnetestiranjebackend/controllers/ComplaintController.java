package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.ComplaintDTO;
import com.example.mobilnetestiranjebackend.DTOs.UserComplaintDTO;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.exceptions.InvalidEnumValueException;
import com.example.mobilnetestiranjebackend.model.Admin;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.ComplaintService;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<?> createUserComplaint(@PathVariable("reportedId") Long reservationId,
                                                 @AuthenticationPrincipal User user,
                                                 @RequestBody TextNode reason){

        complaintService.createUserComplaint(user.getId(), reservationId, reason.asText(), user.getRole());

        return new ResponseEntity<>(("Successfully created new review complaint"), HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('OWNER')")
    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<?> createReviewComplaint(@PathVariable("reviewId") Long reviewId,
                                                   @AuthenticationPrincipal Owner owner,
                                                   @RequestBody String reason){

        ComplaintDTO complaint = complaintService.createReviewComplaint(owner.getId(), reviewId, reason);

        return ResponseEntity.ok().body(complaint);

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
                                                 @AuthenticationPrincipal Admin admin){

        if(status.equals(RequestStatus.PENDING)) throw new InvalidEnumValueException("Invalid operation");

        complaintService.reviewUserComplaint(complaintId, status);

        return new ResponseEntity<>(("Successfully reviewed a review complaint"), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/getUserComplaints")
    public ResponseEntity<?> getUserComplaints(@RequestParam(defaultValue = "0") int pageNo,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               @AuthenticationPrincipal Admin admin) {

        Page<UserComplaintDTO> complaints = complaintService.getUserComplaints(pageNo, pageSize);
        return new ResponseEntity<>(complaints, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('OWNER')")
    @GetMapping("{complaintId}")
    public ResponseEntity<?> getComplaint(@PathVariable("complaintId") Long complaintId,
                                          @AuthenticationPrincipal User user) {

        ComplaintDTO complaint = complaintService.getComplaint(complaintId, user.getId());

        return ResponseEntity.ok().body(complaint);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("")
    public ResponseEntity<?> getAllComplaints(@AuthenticationPrincipal Admin admin,
                                              @RequestParam(defaultValue = "0") int pageNo,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        Page<ComplaintDTO> complaintDTOPage = complaintService.getComplaints(admin.getId(), pageNo, pageSize);


        return ResponseEntity.ok().body(complaintDTOPage);
    }
}
