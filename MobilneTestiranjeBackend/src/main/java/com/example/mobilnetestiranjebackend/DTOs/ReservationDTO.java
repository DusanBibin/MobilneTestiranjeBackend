package com.example.mobilnetestiranjebackend.DTOs;

import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDTO {

    private Long availabilityId;
    private Long reservationId;
    private Long accommodationId;
    @FutureOrPresent(message = "Start date must be in the future")
    @NotNull(message = "Start date must be provided")
    private LocalDate reservationStartDate;

    @Future(message = "End date must be in the future")
    @NotNull(message = "End date must be provided")
    private LocalDate reservationEndDate;

    @NotNull(message = "Accommodation price for this period must be provided")
    @Min(value = 1, message = "There must be at least one guest")
    private Long guestNum;


    private LocalDate cancelDeadline;
    private String accommodationAddress;
    private Long unitPrice;
    private Boolean perGuest;

    private Long ownerId;
    private String ownerNameAndSurname;
    private String ownerEmail;


    private Long guestId;
    private String nameAndSurname;
    private String userEmail;
    private Long timesUserCancel;
    private Boolean conflictReservations;

    private String accommodationName;
    private Long price;
    private ReservationStatus status;
    private String reason;
    private Boolean reviewPresent;

}
