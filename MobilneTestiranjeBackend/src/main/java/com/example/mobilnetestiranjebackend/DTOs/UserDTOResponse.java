package com.example.mobilnetestiranjebackend.DTOs;

import com.example.mobilnetestiranjebackend.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTOResponse {
    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address;

    private Role role;
}
