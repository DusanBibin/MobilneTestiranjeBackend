package com.example.mobilnetestiranjebackend.DTOs;

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
public class ChangePasswordDTO {

    @NotBlank(message = "Password is required")
    private String currentPassword;

    @NotBlank(message = "Password is required")
    @Size(min = 10, message = "Password must be at least 10 characters")
    private String newPassword;

    @NotBlank(message = "Repeat password is required")
    @Size(min = 10, message = "Password must be at least 10 characters")
    private String repeatNewPassword;
}
