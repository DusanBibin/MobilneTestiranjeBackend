package com.example.mobilnetestiranjebackend.DTOs;


import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationDTO {
    @NotBlank(message = "Accommodation name must be present")
    private String name;

    @NotBlank(message = "Description must be present")
    private String description;

    @NotBlank(message = "Address must be present")
    private String address;

    @NotNull
    @DecimalMin(value = "-90.0", inclusive = true, message = "Latitude must be between -90.0 and 90.0")
    @DecimalMax(value = "90.0", inclusive = true, message = "Latitude must be between -90.0 and 90.0")
    private Double lat;

    @NotNull
    @DecimalMin(value = "-180.0", inclusive = true, message = "Longitude must be between -180.0 and 180.0")
    @DecimalMax(value = "180.0", inclusive = true, message = "Longitude must be between -180.0 and 180.0")
    private Double lon;

    @NotNull(message = "Amenities must be present")
    private List<Amenity> amenities;

    @NotNull(message = "Min number of guests must be present")
    @Min(value = 1, message = "Min guests must be at least 1")
    private Integer minGuests;

    @NotNull(message = "Max number of guests must be present")
    @Min(value = 1, message = "Max guests must be at least 1")
    private Integer maxGuests;

    @NotNull(message = "Accommodation type must be present")
    private AccommodationType accommodationType;

    @NotNull(message = "Auto accept must option must be present")
    private Boolean autoAcceptEnabled;

    @Valid
    @NotEmpty(message = "There must be at least one availability period")
    private List<@Valid AccommodationAvailabilityDTO> availabilityList;

    private List<String> imagePaths;

    private RequestStatus status;
    private String reason;
}
