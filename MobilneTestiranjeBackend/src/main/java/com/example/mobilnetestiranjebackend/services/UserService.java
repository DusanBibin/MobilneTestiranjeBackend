package com.example.mobilnetestiranjebackend.services;


import com.example.mobilnetestiranjebackend.DTOs.ChangePasswordDTO;
import com.example.mobilnetestiranjebackend.DTOs.UserDTO;
import com.example.mobilnetestiranjebackend.DTOs.UserDTOResponse;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthenticationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidAuthorizationException;
import com.example.mobilnetestiranjebackend.exceptions.InvalidInputException;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTOResponse getUserInfo(User user) {

        return UserDTOResponse.builder()
                .email(user.getEmail())
                .role(user.getRole())
                .address(user.getAddress())
                .firstName(user.getFirstName())
                .lastName(user.getLastname())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public void changeUserInfo(UserDTO userDTO, User user) {

        user.setFirstName(userDTO.getFirstName());
        user.setAddress(userDTO.getAddress());
        user.setLastname(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        userRepository.save(user);
    }

    public void changeUserPassword(ChangePasswordDTO changePasswordDTO, User user) {


        if(!user.getPassword().equals(passwordEncoder.encode(changePasswordDTO.getCurrentPassword())))
            throw new InvalidAuthenticationException("Current password is incorrect");

        if(!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getRepeatNewPassword()))
            throw new InvalidInputException("Passwords do not match");

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));

        userRepository.save(user);

    }


}
