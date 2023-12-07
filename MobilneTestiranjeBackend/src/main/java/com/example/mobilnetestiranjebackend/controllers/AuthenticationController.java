package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponseDTO;
import com.example.mobilnetestiranjebackend.DTOs.RegisterRequestDTO;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request){

        if(!request.getPassword().equals(request.getRepeatPassword()))
            return ResponseEntity.badRequest().body("Passwords do not match");

        if(!request.getRole().equals(Role.GUEST.toString()) && !request.getRole().equals(Role.OWNER.toString()))
            return ResponseEntity.badRequest().body("Invalid user role selected");

        if(authService.userExist(request.getEmail()))
            return ResponseEntity.badRequest().body("User with email " + request.getEmail() + " already exists");

        authService.register(request);
        return ResponseEntity.ok().body("Success, user confirmation email was sent to " + request.getEmail());
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> register(
            @RequestBody AuthenticationRequestDTO request
    ){
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
