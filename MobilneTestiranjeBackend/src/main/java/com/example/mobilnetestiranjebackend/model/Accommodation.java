package com.example.mobilnetestiranjebackend.model;

import com.example.mobilnetestiranjebackend.enums.AccommodationType;
import com.example.mobilnetestiranjebackend.enums.Amenity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accommodation")
public class  Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String address;
    private Double lat;
    private Double lon;

    private Long minGuests;
    private Long maxGuests;

    private AccommodationType accommodationType;

    private Boolean autoAcceptEnabled;

    @ElementCollection
    private List<Amenity> amenities;



    @ElementCollection
    private List<String> imagePaths;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accommodation")
    private List<Availability> availabilityList;













    @ManyToOne
    private Owner owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accommodation")
    private List<Reservation> reservations;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accommodation")
    private List<AccommodationReview> accommodationReviews;

}
