package com.example.mobilnetestiranjebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "availability")
public class Availability {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Accommodation accommodation;

    private LocalDate startDate;
    private LocalDate endDate;
    private Long price;
    private LocalDate cancelDeadline;
    private Boolean pricePerGuest;
}
