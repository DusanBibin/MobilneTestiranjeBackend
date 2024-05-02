package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponseDTO;
import com.example.mobilnetestiranjebackend.DTOs.RegisterRequestDTO;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.exceptions.InvalidEnumValueException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidRepeatPasswordException;
import com.example.mobilnetestiranjebackend.exceptions.UserAlreadyExistsException;
import com.example.mobilnetestiranjebackend.services.AuthenticationService;
import com.example.mobilnetestiranjebackend.services.UserService;
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

        if(request.getRole().equals(Role.ADMIN)) throw new InvalidEnumValueException("You cannot register as an admin");


        authService.userExist(request.getEmail(), request.getPhoneNumber());

        authService.register(request);

        return new ResponseEntity<>(("Verification email has been sent to " + request.getEmail()), HttpStatus.OK);
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

    @GetMapping("/{email}/check-sms-code/{smsCode}")
    public ResponseEntity<?> checkSendSms(@PathVariable("smsCode") String smsCode,
                                          @PathVariable("email") String email){


        authService.checkVerificationSms(smsCode, email);

        return new ResponseEntity<>(("Confirmation email has been sent to " + email), HttpStatus.OK);
    }


}
