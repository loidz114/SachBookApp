package com.example.sachbook.data.model;

public class CartItemRequest {
    private Long bookId;
    private Integer quantity;

    public CartItemRequest(Long bookId, Integer quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    // Getter và Setter
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}