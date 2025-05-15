package com.example.sachbook.data.model;
import java.io.Serializable;

public class RegisterRequest implements Serializable {
    private String username;
    private String name;
    private String phone;
    private String email;
    private String address;
    private String password;

    public RegisterRequest(String username, String name, String phone, String email, String address, String password) {
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.password = password;
    }

    // Getters
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPassword() { return password; }

    // Setters (Retrofit cần setters để serialize)
    public void setUsername(String username) { this.username = username; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setPassword(String password) { this.password = password; }
}