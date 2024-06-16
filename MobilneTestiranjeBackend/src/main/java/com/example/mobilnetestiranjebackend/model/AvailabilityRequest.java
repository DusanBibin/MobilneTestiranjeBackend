package com.example.mobilnetestiranjebackend.model;

import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import com.example.mobilnetestiranjebackend.enums.RequestType;
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
@Table(name = "availability_request")
public class AvailabilityRequest {
    @Id
    @GeneratedValue
    private Long id;


    private LocalDate startDate;
    private LocalDate endDate;
    private Long price;
    private LocalDate cancelDeadline;
    private Boolean pricePerGuest;
    
    @ManyToOne
    private Availability availability;

//    @ManyToOne
//    private Accommodation accommodation;
//
//    private RequestStatus status;
//
//    private String reason;

    private RequestType requestType;
}
