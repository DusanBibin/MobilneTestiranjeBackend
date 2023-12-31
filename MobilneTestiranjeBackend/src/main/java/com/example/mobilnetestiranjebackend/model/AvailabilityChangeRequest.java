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
public class AvailabilityChangeRequest {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private AccommodationAvailability accommodationAvailability;

    private LocalDate startDate;
    private LocalDate endDate;
    private Long price;
    private LocalDate cancelDeadline;
    private RequestStatus status;
}
