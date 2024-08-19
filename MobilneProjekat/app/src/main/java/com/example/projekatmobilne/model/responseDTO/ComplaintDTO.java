package com.example.projekatmobilne.model.responseDTO;

import com.example.projekatmobilne.model.Enum.RequestStatus;

public class  ComplaintDTO {
    private String ownerNameSurname;
    private String ownerEmail;
    private String guestNameSurname;
    private String guestEmail;
    private String reviewComment;
    private Long reviewRating;
    private String complaintReason;
    private RequestStatus requestStatus;
    private String adminResponse;
    private String reviewType;

    private Long complaintId;
    private Long reviewId;
    private Long accommodationId;
    private Long reservationId;


    public ComplaintDTO(String ownerNameSurname, String ownerEmail, String guestEmail,
                        String reviewComment, String complaintReason, RequestStatus requestStatus,
                        String adminResponse, String guestNameSurname, Long reviewRating,
                        String reviewType, Long complaintId, Long reviewId, Long accommodationId,
                        Long reservationId) {
        this.ownerNameSurname = ownerNameSurname;
        this.ownerEmail = ownerEmail;
        this.guestEmail = guestEmail;
        this.reviewComment = reviewComment;
        this.complaintReason = complaintReason;
        this.requestStatus = requestStatus;
        this.adminResponse = adminResponse;
        this.guestNameSurname = guestNameSurname;
        this.reviewRating = reviewRating;
        this.reviewType = reviewType;

        this.complaintId = complaintId;
        this.reviewId = reviewId;
        this.accommodationId = accommodationId;
        this.reservationId = reservationId;
    }


    public String getOwnerNameSurname() {
        return ownerNameSurname;
    }

    public void setOwnerNameSurname(String ownerNameSurname) {
        this.ownerNameSurname = ownerNameSurname;
    }

    public String getGuestNameSurname() {
        return guestNameSurname;
    }

    public void setGuestNameSurname(String guestNameSurname) {
        this.guestNameSurname = guestNameSurname;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public Long getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(Long reviewRating) {
        this.reviewRating = reviewRating;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }

    public String getComplaintReason() {
        return complaintReason;
    }

    public void setComplaintReason(String complaintReason) {
        this.complaintReason = complaintReason;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getAccommodationId() {
        return accommodationId;
    }

    public void setAccommodationId(Long accommodationId) {
        this.accommodationId = accommodationId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public String toString() {
        return "ComplaintDTO{" +
                "ownerNameSurname='" + ownerNameSurname + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                ", guestNameSurname='" + guestNameSurname + '\'' +
                ", guestEmail='" + guestEmail + '\'' +
                ", reviewComment='" + reviewComment + '\'' +
                ", reviewRating=" + reviewRating +
                ", complaintReason='" + complaintReason + '\'' +
                ", requestStatus=" + requestStatus +
                ", adminResponse='" + adminResponse + '\'' +
                ", reviewType='" + reviewType + '\'' +
                ", complaintId=" + complaintId +
                ", reviewId=" + reviewId +
                ", accommodationId=" + accommodationId +
                ", reservationId=" + reservationId +
                '}';
    }
}
