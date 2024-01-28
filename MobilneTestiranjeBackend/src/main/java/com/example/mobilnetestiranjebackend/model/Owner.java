package com.example.mobilnetestiranjebackend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Owner extends User {

    @OneToMany(fetch = FetchType.LAZY)
    private List<Accommodation> accommodations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccommodationRequest> accommodationRequests;

}
