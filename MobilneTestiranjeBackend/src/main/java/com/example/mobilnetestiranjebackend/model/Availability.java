package com.example.mobilnetestiranjebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accommodation_availability")
@ToString
public class Availability {
    @Id
    @GeneratedValue
    private Long id;

    @ToString.Exclude
    @ManyToOne
    private Accommodation accommodation;

    private LocalDate startDate;
    private LocalDate endDate;
    private Long price;
    private LocalDate cancelDeadline;
    private Boolean pricePerGuest;
}
