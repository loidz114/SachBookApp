package com.example.sachbook.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CartModel {
    @SerializedName("id")
    private Long id;

    @SerializedName("user")
    private UserModel user;

    @SerializedName("items")
    private List<CartItemModel> items;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public List<CartItemModel> getItems() {
        return items != null ? items : new ArrayList<>();
    }

    public void setItems(List<CartItemModel> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "CartModel{id=" + id + ", user=" + user + ", items=" + (items != null ? items.size() : "null") + "}";
    }
}