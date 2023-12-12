package com.example.mobilnetestiranjebackend.DTOs;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationAvailabilityDTO {

    @FutureOrPresent(message = "Start date must be in the future")
    @NotNull(message = "Start date must be provided")
    private LocalDate startDate;

    @Future(message = "End date must be in the future")
    @NotNull(message = "End date must be provided")
    private LocalDate endDate;

    @NotNull(message = "Accommodation price for this period must be provided")
    @Min(value = 1, message = "Price must be at least 1")
    private Double price;
}
