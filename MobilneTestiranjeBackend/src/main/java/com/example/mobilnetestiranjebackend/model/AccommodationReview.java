package com.example.mobilnetestiranjebackend.model;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accommodation_reviews")
public class AccommodationReview extends Review{
    @ManyToOne
    private Guest guest;

    @ManyToOne
    private Accommodation accommodation;

    private Boolean allowed;
}
