package com.example.mobilnetestiranjebackend.DTOs;


import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccommodationSearchDTO {
    private Long accommodationId;
    private String name;
    private String address;
    private List<Amenity> amenities;
    private Long totalPrice;
    private Long oneNightPrice;
    private Boolean isPerPerson;
    private Long minGuests;
    private Long maxGuests;
    private AccommodationType accommodationType;
    private Double rating;
}
