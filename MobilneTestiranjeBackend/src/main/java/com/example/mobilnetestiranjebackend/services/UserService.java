package com.example.mobilnetestiranjebackend.services;

import com.example.mobilnetestiranjebackend.DTOs.ErrorDTO;
import com.example.mobilnetestiranjebackend.DTOs.UserDTO;
import com.example.mobilnetestiranjebackend.model.User;
import com.example.mobilnetestiranjebackend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUser(String email) {
        return userRepository.findByEmail(email);

    }

    public Optional<User> getUser(int id) {
        return userRepository.findById(id);

    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User editUser(User user, UserDTO userDTO) {
        if (user != null) {
            user.setFirstName(userDTO.getFirstName());
            user.setLastname(userDTO.getLastname());
            user.setPhoneNumber(userDTO.getPhoneNumber());
            user.setAddress(userDTO.getAddress());
            user.setEmail(userDTO.getEmail());
            return this.save(user);
        } else {
            return null;
        }
    }
}
