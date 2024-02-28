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
@ToString
public class  Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private Long minGuests;
    private Long maxGuests;

    private AccommodationType accommodationType;

    private Boolean autoAcceptEnabled;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accommodation")
    private List<Availability> availabilityList;

    @ManyToOne
    private Owner owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accommodation")
    private List<Reservation> reservations;

}
