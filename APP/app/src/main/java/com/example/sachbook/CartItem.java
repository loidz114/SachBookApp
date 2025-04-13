package com.example.sachbook;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private String bookId;
    private String bookTitle;
    private double price;
    private int quantity;

    // Constructor
    public CartItem(String bookId, String bookTitle, double price, int quantity) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.price = price;
        this.quantity = quantity;
    }

    // Constructor cho Parcelable
    protected CartItem(Parcel in) {
        bookId = in.readString();
        bookTitle = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
    }

    // Creator cho Parcelable
    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookId);
        dest.writeString(bookTitle);
        dest.writeDouble(price);
        dest.writeInt(quantity);
    }

    // Getters và Setters
    public String getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}