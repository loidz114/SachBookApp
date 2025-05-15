package com.example.sachbook.data.model;

public class OrderRequest {
    private String paymentMethod;
    private String shippingAddress;
    private String discountCode;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        if (paymentMethod != null && paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Phương thức thanh toán không được để trống");
        }
        if (paymentMethod != null && paymentMethod.length() > 50) {
            throw new IllegalArgumentException("Phương thức thanh toán phải dưới 50 ký tự");
        }
        this.paymentMethod = paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        if (shippingAddress != null && shippingAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Địa chỉ giao hàng không được để trống");
        }
        if (shippingAddress != null && shippingAddress.length() > 255) {
            throw new IllegalArgumentException("Địa chỉ giao hàng phải dưới 255 ký tự");
        }
        this.shippingAddress = shippingAddress;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        if (discountCode != null && discountCode.length() > 20) {
            throw new IllegalArgumentException("Mã giảm giá phải dưới 20 ký tự");
        }
        this.discountCode = discountCode;
    }
}