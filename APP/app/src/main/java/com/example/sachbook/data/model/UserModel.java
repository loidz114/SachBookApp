package com.example.sachbook.data.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    private Long id;
    private String username;
    private String email;
    @SerializedName("name") // Matches backend field
    private String fullName;
    private String phone;
    private String address;
    private String token;

    // Constructor
    public UserModel() {}

    public UserModel(Long id, String username, String email, String fullName, String phone, String address, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.token = token;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}