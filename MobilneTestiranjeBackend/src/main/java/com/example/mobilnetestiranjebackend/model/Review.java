package com.example.mobilnetestiranjebackend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
@Inheritance(strategy = InheritanceType.JOINED)
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    private String comment;
    private Long rating;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Complaint complaint;

    private Boolean allowed;
}
