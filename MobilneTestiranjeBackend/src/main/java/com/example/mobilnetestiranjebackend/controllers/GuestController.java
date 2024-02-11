package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.model.Guest;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.services.GuestService;
import com.example.mobilnetestiranjebackend.services.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/guest")
@RequiredArgsConstructor
public class GuestController {
    private final GuestService guestService;


    @PreAuthorize("hasAuthority('GUEST')")
    @DeleteMapping(path = "/delete-account")
    public ResponseEntity<?> changeUserPassword(@AuthenticationPrincipal Guest guest){

        guestService.deleteAccount(guest);

        return new ResponseEntity<>(("Successfully deleted account"), HttpStatus.OK);

    }
}
