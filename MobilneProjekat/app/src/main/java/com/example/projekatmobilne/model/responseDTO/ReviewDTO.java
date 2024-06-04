package com.example.projekatmobilne.model.responseDTO;

public class ReviewDTO {
    private String comment;
    private Long rating;
    public ReviewDTO(String comment, Long rating) {
        this.comment = comment;
        this.rating = rating;
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

    @Override
    public String toString() {
        return "ReviewDTO{" +
                "comment='" + comment + '\'' +
                ", rating=" + rating +
                '}';
    }
}
