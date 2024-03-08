package com.example.mobilnetestiranjebackend.model;


import jakarta.persistence.*;
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
@Table(name = "review_complaints")
public class ReviewComplaint extends Complaint{

    @ManyToOne
    private Owner owner;

    @OneToOne
    private Review review;

    @ManyToOne
    private Guest guest;

}
