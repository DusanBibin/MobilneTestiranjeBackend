package com.example.mobilnetestiranjebackend.model;


import com.example.mobilnetestiranjebackend.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = {"guest", "accommodation", "availability"})
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate reservationStartDate;
    private LocalDate reservationEndDate;
    private LocalDate cancelDeadline;
    private Long guestNum;
    private Long price;
    private Long unitPrice;
    private Boolean perGuest;
    private ReservationStatus status;
    private String reason;

    @ManyToOne
    private Guest guest;
    @ManyToOne
    private Accommodation accommodation;
    @ManyToOne
    @JoinColumn(name = "availability_id", nullable = true)
    private Availability availability;
}
