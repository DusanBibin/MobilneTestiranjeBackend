package com.example.mobilnetestiranjebackend.controllers;

import com.example.mobilnetestiranjebackend.DTOs.AuthenticationRequestDTO;
import com.example.mobilnetestiranjebackend.DTOs.ErrorDTO;
import com.example.mobilnetestiranjebackend.DTOs.SuccessDTO;
import com.example.mobilnetestiranjebackend.DTOs.UserDTO;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user/")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping(value = "{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        Optional<User> userWrapper = userService.getUser(id);
        if (userWrapper.isPresent()) {
            User user = userWrapper.get();
            return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
        } else {
            ErrorDTO dto = new ErrorDTO("User with id " + id + " not found");
            return new ResponseEntity<ErrorDTO>(dto, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "{id}")
    public ResponseEntity<?> editUser(@PathVariable("id") int id, @RequestBody UserDTO userDTO) {
        User user = userService.editUser(id, userDTO);
        if (user != null) {
            return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
        } else {
            ErrorDTO dto = new ErrorDTO("User with id " + id + " not found");
            return new ResponseEntity<ErrorDTO>(dto, HttpStatus.BAD_REQUEST);
        }
    }
}
