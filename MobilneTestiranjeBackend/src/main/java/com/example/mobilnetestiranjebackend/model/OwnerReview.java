package com.example.mobilnetestiranjebackend.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "owner_reviews")
public class OwnerReview extends Review {

    @ManyToOne
    private Guest guest;

    @ManyToOne
    private Owner owner;


}
