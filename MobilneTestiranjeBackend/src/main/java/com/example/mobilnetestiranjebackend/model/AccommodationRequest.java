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
@Table(name = "accommodation_request")
public class AccommodationRequest {
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
    private List<String> imagePaths;

    private Integer minGuests;
    private Integer maxGuests;

    private AccommodationType accommodationType;

    private Boolean autoAcceptEnabled;

    @ManyToOne
    private Accommodation accommodation;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilityRequest> availabilityRequests;

    @ManyToOne
    private Owner owner;

    private RequestStatus status;
}
