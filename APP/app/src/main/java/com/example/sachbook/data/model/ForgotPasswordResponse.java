package com.example.sachbook.data.model;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public ForgotPasswordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}