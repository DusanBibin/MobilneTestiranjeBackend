package com.example.mobilnetestiranjebackend.DTOs;

import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationDTOResponse {
    private Long id;
    private String name;

    private String description;

    private String address;

    private Double lat;

    private Double lon;

    private List<Amenity> amenities;

    private Long minGuests;

    private Long maxGuests;

    private AccommodationType accommodationType;

    private Boolean autoAcceptEnabled;

    private List<@Valid AvailabilityDTO> availabilityList;

    private List<Long> imageIds;

    private RequestStatus status;
    private String reason;
}
