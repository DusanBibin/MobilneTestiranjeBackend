package com.example.projekatmobilne.model.responseDTO.paging.PagingDTOs.PageTypes.innerDTOPage;

public class ReviewDTOPageItemInner {
    private Long reviewId;
    private String comment;
    private Long rating;
    private String complaintReason;
    private Long complaintId;
    public ReviewDTOPageItemInner(String comment, Long rating) {
        this.comment = comment;
        this.rating = rating;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public String getComplaintReason() {
        return complaintReason;
    }

    public void setComplaintReason(String complaintReason) {
        this.complaintReason = complaintReason;
    }

    @Override
    public String toString() {
        return "ReviewDTOPageItemInner{" +
                "reviewId=" + reviewId +
                ", comment='" + comment + '\'' +
                ", rating=" + rating +
                ", complaintReason='" + complaintReason + '\'' +
                ", complaintId=" + complaintId +
                '}';
    }
}
