package com.example.sachbook; // Thay bằng package của bạn

public class CartItem {
    private String bookId;
    private String bookTitle;
    private double price;
    private int quantity;

    public CartItem(String bookId, String bookTitle, double price, int quantity) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.price = price;
        this.quantity = quantity;
    }

    public String getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}