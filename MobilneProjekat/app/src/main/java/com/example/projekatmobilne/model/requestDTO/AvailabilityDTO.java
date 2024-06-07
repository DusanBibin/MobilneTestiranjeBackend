package com.example.projekatmobilne.model.requestDTO;

import com.example.projekatmobilne.model.Enum.RequestStatus;
import com.example.projekatmobilne.model.Enum.RequestType;

import java.time.LocalDate;

public class AvailabilityDTO {
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate cancellationDeadline;

    private Boolean pricePerGuest;

    private Long price;

    private RequestType requestType;

    private RequestStatus status;
    private String reason;
}
