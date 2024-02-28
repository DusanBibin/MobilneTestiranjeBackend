package com.example.mobilnetestiranjebackend.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "owner_reviews")
@ToString
public class OwnerReview {
    @Id
    @GeneratedValue
    private Long id;

    private String comment;
    private Long rating;

    @ManyToOne
    private Guest guest;

    @ManyToOne
    private Owner owner;
}
