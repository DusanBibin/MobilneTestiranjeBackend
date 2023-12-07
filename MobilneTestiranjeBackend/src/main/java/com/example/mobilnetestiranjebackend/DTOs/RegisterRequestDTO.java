package com.example.mobilnetestiranjebackend.DTOs;


import jakarta.validation.constraints.Email;
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
public class RegisterRequestDTO {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email address")
    private String email;

    @Size(min = 10, message = "Password must be at least 10 characters")
    private String password;

    @Size(min = 10, message = "Phone number must be at least 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    private String address;

    @Size(min = 10, message = "Password must be at least 10 characters")
    private String repeatPassword;

    @NotBlank(message = "Role is required")
    private String role;
}
