package com.example.mobilnetestiranjebackend.controllers;


import com.example.mobilnetestiranjebackend.DTOs.ChangePasswordDTO;
import com.example.mobilnetestiranjebackend.DTOs.UserDTO;
import com.example.mobilnetestiranjebackend.exceptions.NonExistingEntityException;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.repositories.UserRepository;
import com.example.mobilnetestiranjebackend.services.UserService;
import com.sendgrid.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User user){

        var userDTOResponse = userService.getUserInfo(user);

        return new ResponseEntity<>((userDTOResponse), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('OWNER') or hasAuthority('GUEST') or hasAuthority('ADMIN')")
    @PutMapping("/change-info")
    public ResponseEntity<?> changeUserInfo(@RequestBody UserDTO userDTO, @AuthenticationPrincipal User user){

        userService.changeUserInfo(userDTO, user);

        return new ResponseEntity<>((userDTO), HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('OWNER') or hasAuthority('GUEST') or hasAuthority('ADMIN')")
    @PutMapping("/change-email")
    public ResponseEntity<?> changeEmail(@RequestBody UserDTO userDTO, @AuthenticationPrincipal User user){


        return new ResponseEntity<>((userDTO), HttpStatus.OK);

    }

    @PreAuthorize("hasAuthority('OWNER') or hasAuthority('GUEST')")
    @PutMapping("/change-phone-number")
    public ResponseEntity<?> changePhoneNumber(@RequestBody UserDTO userDTO, @AuthenticationPrincipal User user){


        return new ResponseEntity<>((userDTO), HttpStatus.OK);

    }

    @PutMapping(path = "/change-password")
    public ResponseEntity<?> changeUserPassword(@RequestBody ChangePasswordDTO changePasswordDTO, @AuthenticationPrincipal User user){

        userService.changeUserPassword(changePasswordDTO, user);

        return new ResponseEntity<>((changePasswordDTO), HttpStatus.OK);

    }


}
