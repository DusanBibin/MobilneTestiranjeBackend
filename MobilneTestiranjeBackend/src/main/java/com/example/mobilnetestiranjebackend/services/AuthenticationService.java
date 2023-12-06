package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequest;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponse;
import com.example.mobilnetestiranjebackend.DTOs.RegisterRequest;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public Boolean userExist(String email){
        var userWrapper = userRepository.findByEmail(email);
        return userWrapper.isPresent();
    }

    public void register(RegisterRequest request) {

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .emailConfirmed(false)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();

        userRepository.save(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(); //TODO dodaj custom exception
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
