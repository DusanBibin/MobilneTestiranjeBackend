package com.example.mobilnetestiranjebackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accommodation_availability")
public class AccommodationAvailability {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Accommodation accommodation;

    private LocalDate startDate;
    private LocalDate endDate;
    private Long price;

}
