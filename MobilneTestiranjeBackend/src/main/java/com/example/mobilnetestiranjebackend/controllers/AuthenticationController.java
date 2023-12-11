package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponseDTO;
import com.example.mobilnetestiranjebackend.DTOs.RegisterRequestDTO;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.exceptions.EmailNotConfirmedException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidRepeatPasswordException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidUserRoleException;
import com.example.mobilnetestiranjebackend.exceptions.UserAlreadyExistsException;
import com.example.mobilnetestiranjebackend.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request){

        authService.checkInput(request);

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
