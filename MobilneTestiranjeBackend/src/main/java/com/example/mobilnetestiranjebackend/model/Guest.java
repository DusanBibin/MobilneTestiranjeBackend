package com.example.mobilnetestiranjebackend.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = {"reservations", "ownerReviews", "accommodationReviews", "favorites", "reviewComplaints"})
@Table(name = "guest")
public class Guest extends User {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "guest")
    private List<Reservation> reservations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "guest")
    private List<OwnerReview> ownerReviews;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "guest")
    private List<AccommodationReview> accommodationReviews;

    @OneToMany
    private List<Accommodation> favorites;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "guest")
    private List<ReviewComplaint> reviewComplaints;


}
