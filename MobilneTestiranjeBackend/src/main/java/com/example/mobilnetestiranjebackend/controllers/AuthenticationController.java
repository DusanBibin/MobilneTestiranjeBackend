package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponseDTO;
import com.example.mobilnetestiranjebackend.DTOs.RegisterRequestDTO;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.exceptions.InvalidEnumValueException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidRepeatPasswordException;
import com.example.mobilnetestiranjebackend.exceptions.UserAlreadyExistsException;
import com.example.mobilnetestiranjebackend.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request){

        if(!request.getPassword().equals(request.getRepeatPassword()))
            throw new InvalidRepeatPasswordException("Passwords do not match");

        if(!request.getRole().equals(Role.GUEST.toString()) && !request.getRole().equals(Role.OWNER.toString()))
            throw new InvalidEnumValueException("Invalid user role selected");

        try {
            Role role = Role.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new InvalidEnumValueException("Invalid user role value");
        }

        if(authService.userExist(request.getEmail()))
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");

        authService.register(request);

        return ResponseEntity.ok().body("Success, user confirmation email was sent to " + request.getEmail());
    }


    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequestDTO request){

        AuthenticationResponseDTO token = authService.authenticate(request);

        return ResponseEntity.ok(token);
    }

    @GetMapping(value = "/activate/{idActivation}")
    public ResponseEntity<String> activateUserEmail(@PathVariable("idActivation") String verificationCode) {

        authService.verifyUser(verificationCode);
        return new ResponseEntity<>(("Account activated!"), HttpStatus.OK);
    }


}
