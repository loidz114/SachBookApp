package com.example.sachbook.data.model;

import com.google.gson.annotations.SerializedName;

public class DiscountModel {
    @SerializedName("id")
    private Long id;

    @SerializedName("code")
    private String code;

    @SerializedName("description")
    private String description;

    @SerializedName("discountPercent")
    private Double discountPercent;

    @SerializedName("discountAmount")
    private Double discountAmount;

    @SerializedName("startDate")
    private String startDate; // Dùng String vì Date từ API sẽ là chuỗi JSON

    @SerializedName("endDate")
    private String endDate;   // Dùng String vì Date từ API sẽ là chuỗi JSON

    @SerializedName("isActive")
    private Boolean isActive;

    @SerializedName("maxUsage")
    private Integer maxUsage;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Double discountPercent) { this.discountPercent = discountPercent; }
    public Double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(Double discountAmount) { this.discountAmount = discountAmount; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Integer getMaxUsage() { return maxUsage; }
    public void setMaxUsage(Integer maxUsage) { this.maxUsage = maxUsage; }
}