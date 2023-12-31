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
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;
    private Integer guestNum;
    private RequestStatus status;

    @ManyToOne
    private Accommodation accommodation;
    @ManyToOne
    private AccommodationAvailability accommodationAvailability;
}
