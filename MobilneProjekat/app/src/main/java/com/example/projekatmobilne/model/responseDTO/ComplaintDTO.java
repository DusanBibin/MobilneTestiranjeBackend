package com.example.projekatmobilne.model.responseDTO;

import com.example.projekatmobilne.model.Enum.RequestStatus;

public class ComplaintDTO {
    private String ownerNameSurname;
    private String ownerEmail;
    private String guestNameSurname;
    private String guestEmail;
    private String reviewComment;
    private Long reviewRating;
    private String complaintReason;
    private RequestStatus requestStatus;
    private String declineReason;


    public ComplaintDTO(String ownerNameSurname, String ownerEmail, String guestEmail,
                        String reviewComment, String complaintReason, RequestStatus requestStatus,
                        String declineReason, String guestNameSurname, Long reviewRating) {
        this.ownerNameSurname = ownerNameSurname;
        this.ownerEmail = ownerEmail;
        this.guestEmail = guestEmail;
        this.reviewComment = reviewComment;
        this.complaintReason = complaintReason;
        this.requestStatus = requestStatus;
        this.declineReason = declineReason;
        this.guestNameSurname = guestNameSurname;
        this.reviewRating = reviewRating;
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

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }

    public String getComplaintReason() {
        return complaintReason;
    }

    public void setComplaintReason(String complaintReason) {
        this.complaintReason = complaintReason;
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
                ", declineReason='" + declineReason + '\'' +
                '}';
    }
}
