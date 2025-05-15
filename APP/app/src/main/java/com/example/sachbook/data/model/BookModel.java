package com.example.sachbook.data.model;

import com.google.gson.annotations.Expose;

public class BookModel {
    private Long id;
    @Expose
    private String title;
    @Expose
    private String author;
    @Expose
    private String description;
    @Expose
    private Double price;
    @Expose
    private Integer quantity;
    @Expose
    private String imageUrl;
    private CategoryModel category; // Excluded from serialization
    @Expose
    private DiscountModel discount;
    @Expose
    private String createdAt; // Use String for simplicity, adjust if backend sends Date
    @Expose
    private Boolean isAvailable;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public CategoryModel getCategory() { return category; }
    public void setCategory(CategoryModel category) { this.category = category; }
    public DiscountModel getDiscount() { return discount; }
    public void setDiscount(DiscountModel discount) { this.discount = discount; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
}