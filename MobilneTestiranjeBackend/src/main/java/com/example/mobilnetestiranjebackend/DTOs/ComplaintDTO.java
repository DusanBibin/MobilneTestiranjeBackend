package com.example.mobilnetestiranjebackend.DTOs;


import com.example.mobilnetestiranjebackend.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintDTO {
    private String reviewType;
    private String ownerNameSurname;
    private String ownerEmail;
    private String guestNameSurname;
    private String guestEmail;
    private String reviewComment;
    private Long reviewRating;
    private String complaintReason;
    private RequestStatus requestStatus;
    private String adminResponse;

    private Long complaintId;
    private Long reviewId;
    private Long accommodationId;
    private Long reservationId;
}
