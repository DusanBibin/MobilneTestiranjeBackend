package com.example.projekatmobilne.model.responseDTO;

public class ReviewDTOResponse {

    private String guestName;
    private ReviewDTO ownerReview;

    private ReviewDTO accommodationReview;



    public ReviewDTOResponse(ReviewDTO ownerReview, ReviewDTO accommodationReview, String guestName) {
        this.ownerReview = ownerReview;
        this.accommodationReview = accommodationReview;
        this.guestName = guestName;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public ReviewDTO getOwnerReview() {
        return ownerReview;
    }

    public void setOwnerReview(ReviewDTO ownerReview) {
        this.ownerReview = ownerReview;
    }

    public ReviewDTO getAccommodationReview() {
        return accommodationReview;
    }

    public void setAccommodationReview(ReviewDTO accommodationReview) {
        this.accommodationReview = accommodationReview;
    }

    @Override
    public String toString() {
        return "ReviewDTOResponse{" +
                "ownerReview=" + ownerReview +
                ", accommodationReview=" + accommodationReview +
                '}';
    }
}
