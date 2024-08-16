package com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes;

import com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.innerDTOPage.ReviewDTOPageItemInner;

public class ReviewDTOPageItem {

    private String guestName;
    private ReviewDTOPageItemInner ownerReview;

    private ReviewDTOPageItemInner accommodationReview;

    public ReviewDTOPageItem(String guestName) {
        this.guestName = guestName;
    }

    public ReviewDTOPageItem(ReviewDTOPageItemInner ownerReview, ReviewDTOPageItemInner accommodationReview, String guestName) {
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

    public ReviewDTOPageItemInner getOwnerReview() {
        return ownerReview;
    }

    public void setOwnerReview(ReviewDTOPageItemInner ownerReview) {
        this.ownerReview = ownerReview;
    }

    public ReviewDTOPageItemInner getAccommodationReview() {
        return accommodationReview;
    }

    public void setAccommodationReview(ReviewDTOPageItemInner accommodationReview) {
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
