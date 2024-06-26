package com.example.mobilnetestiranjebackend.DTOs;

import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.model.Availability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AccommodationDifferencesDTO {

    private AccommodationDTOEdit accommodationInfo;
    private AccommodationDTOEdit requestAccommodationInfo;
    private List<AvailabilityDTO> availabilities;
    private List<AvailabilityDTO> requestAvailabilities;

    private List<String> imagesToAdd;
    private List<String> imagesToRemove;
    private List<String> currentImages;

    private RequestStatus status;
    private String reason;
}
