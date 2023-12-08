package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.AuthenticationResponseDTO;
import com.example.mobilnetestiranjebackend.DTOs.RegisterRequestDTO;
import com.example.mobilnetestiranjebackend.enums.Role;
import com.example.mobilnetestiranjebackend.exceptions.EmailNotConfirmedException;
import com.example.mobilnetestiranjebackend.exceptions.UserNotFoundException;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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

    public Boolean userEmailConfirmed(String email){
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with this email not found"));
        return user.getEmailConfirmed();
    }

    public void register(RegisterRequestDTO request) {

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastname(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .emailConfirmed(true)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();

        userRepository.save(user);
    }



    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();


        if(!user.getEmailConfirmed()) throw new EmailNotConfirmedException("Email not confirmed for this user");

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDTO.builder()
                .token(jwtToken)
                .build();
    }
}
