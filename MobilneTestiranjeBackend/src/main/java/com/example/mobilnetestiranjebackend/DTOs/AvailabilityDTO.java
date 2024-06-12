package com.example.mobilnetestiranjebackend.DTOs;

import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.RequestType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AvailabilityDTO {
    private Long id;

    @FutureOrPresent(message = "Start date must be in the future")
    @NotNull(message = "Start date must be provided")
    private LocalDate startDate;

    @Future(message = "End date must be in the future")
    @NotNull(message = "End date must be provided")
    private LocalDate endDate;

    @Future(message = "Cancellation date must be in the future")
    @NotNull(message = "Cancellation date must be provided")
    private LocalDate cancellationDeadline;

    @NotNull(message = "Price per guest must be present")
    private Boolean pricePerGuest;

    @NotNull(message = "Accommodation price for this period must be provided")
    @Min(value = 1, message = "Price must be at least 1")
    private Long price;

    private RequestType requestType;

    private RequestStatus status;
    private String reason;
}
