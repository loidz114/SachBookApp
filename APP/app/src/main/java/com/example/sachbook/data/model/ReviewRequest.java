package com.example.sachbook.data.model;

public class ReviewRequest {
    private String comment;
    private Integer rating;

    public ReviewRequest(String comment, Integer rating) {
        this.comment = comment;
        this.rating = rating;
    }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}