package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-complaints")
@RequiredArgsConstructor
public class UserComplaintController {

    private final ComplaintService complaintService;

    @PreAuthorize("hasAuthority('GUEST') or hasAuthority('OWNER')")
    @PostMapping("/users/{reportedId}")
    public ResponseEntity<?> createUserComplaint(@PathVariable("reportedId") Long reportedId,
                                                   @AuthenticationPrincipal User user,
                                                   @RequestBody String reason){

        complaintService.createUserComplaint(user.getId(), reportedId, reason);

        return new ResponseEntity<>(("Successfully created new review complaint"), HttpStatus.OK);

    }
}
