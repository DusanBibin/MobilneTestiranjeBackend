package com.example.mobilnetestiranjebackend.DTOs;


import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationRequestPreviewDTO {
    private Long requestId;
    private String accommodationName;
    private String accommodationAddress;
    private RequestStatus status;
    private String reason;
    private RequestType requestType;
    private String existingAccommodationName;
    private String existingAddress;
}

