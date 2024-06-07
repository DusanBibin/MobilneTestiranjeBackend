package com.example.projekatmobilne.model.requestDTO;


import com.example.projekatmobilne.model.Enum.AccommodationType;
import com.example.projekatmobilne.model.Enum.Amenity;

import java.util.List;

public class AccommodationDTO {
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
    private List<AvailabilityDTO> availabilityList;
}