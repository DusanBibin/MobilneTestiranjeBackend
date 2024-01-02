package com.example.mobilnetestiranjebackend.model;


import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "availability_change")
public class AvailabilityRequest {
    //Ova klasa sluzi za menjanje postojecih availabilitija
    //kada se posalje zahtev za menjanje ovde je status pending i status u accommodationAvailability je pending, ako bude accepted postavljaju se oba
    //na accepted i podaci odavde se cuvaju u pravi accommodationAvailability,
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private AccommodationAvailability accommodationAvailability;

    @ManyToOne
    private Accommodation accommodation;

    private LocalDate startDate;
    private LocalDate endDate;
    private Long price;
    private LocalDate cancelDeadline;
    private Boolean pricePerGuest;

    private RequestStatus status;
    private Boolean isEditRequest;
}
