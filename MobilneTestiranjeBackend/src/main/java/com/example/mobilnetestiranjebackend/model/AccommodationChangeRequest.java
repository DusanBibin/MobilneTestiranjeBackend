package com.example.mobilnetestiranjebackend.model;

import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accommodation_change")
public class AccommodationChangeRequest {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private String address;
    private Double lat;
    private Double lon;


    @ElementCollection
    private List<Amenity> amenities;

    @ElementCollection
    private List<String> photos;

    private Integer minGuests;
    private Integer maxGuests;

    private AccommodationType accommodationType;

    private Boolean approved;

    private Boolean autoAcceptEnabled;

    @ManyToOne
    private Accommodation accommodation;

    private RequestStatus status;
}
