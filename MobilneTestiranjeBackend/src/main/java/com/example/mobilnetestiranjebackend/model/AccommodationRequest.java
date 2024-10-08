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

    private Long minGuests;
    private Long maxGuests;

    private AccommodationType accommodationType;

    @ElementCollection
    private List<Amenity> amenities;

    @ElementCollection
    private List<String> imagePathsNew;
    @ElementCollection
    private List<String> imagesToRemove;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilityRequest> availabilityRequests;




    private RequestStatus status;

    private String reason;



    @ManyToOne
    private Owner owner;

    @ManyToOne
    private Accommodation accommodation;
}
