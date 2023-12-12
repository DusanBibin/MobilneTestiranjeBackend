package com.example.mobilnetestiranjebackend.model;

import com.example.mobilnetestiranjebackend.enums.Amenity;
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
@Table(name = "accommodation")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String address;
    private Long lat;
    private Long lon;


    @ElementCollection
    private List<Amenity> amenities;

    @ElementCollection
    private List<String> photos;

    private int minGuests;
    private int maxGuests;

    private String accommodationType;

    private boolean approved;

    @OneToMany(mappedBy = "accommodation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccommodationAvailability> availabilityList;

}
