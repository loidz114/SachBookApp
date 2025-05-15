package com.example.sachbook.data.model;

public class CartItemModel {
    private Long id;
    private BookModel book;
    private Integer quantity;
    private Double price;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BookModel getBook() { return book; }
    public void setBook(BookModel book) { this.book = book; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}