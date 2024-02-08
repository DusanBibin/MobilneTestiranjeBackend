package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.ChangePasswordDTO;
import com.example.mobilnetestiranjebackend.model.Owner;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @DeleteMapping(path = "/delete-account")
    public ResponseEntity<?> changeUserPassword(@AuthenticationPrincipal Owner owner){

        ownerService.deleteAccount(owner);

        return new ResponseEntity<>(("Successfully deleted account"), HttpStatus.OK);

    }

}
