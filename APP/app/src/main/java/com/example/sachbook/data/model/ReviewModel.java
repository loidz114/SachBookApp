package com.example.sachbook.data.model;

import java.util.Date;

public class ReviewModel {
    private Long id;
    private UserModel user;
    private BookModel book;
    private Integer rating;
    private String comment;
    private Date createdAt;

    // Getters và setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UserModel getUser() { return user; }
    public void setUser(UserModel user) { this.user = user; }
    public BookModel getBook() { return book; }
    public void setBook(BookModel book) { this.book = book; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}