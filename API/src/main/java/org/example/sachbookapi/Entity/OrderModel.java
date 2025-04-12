package org.example.sachbookapi.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người dùng đặt đơn hàng
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    // Danh sách sản phẩm trong đơn
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemModel> orderItems;

    @NotNull
    @Column(nullable = false)
    private Double totalAmount;

    private Double discountAmount;

    @NotNull
    @Column(nullable = false)
    private Double finalAmount;

    // Mã giảm giá áp dụng cho đơn
    @ManyToOne
    @JoinColumn(name = "discount_id")
    private DiscountModel discount;

    @NotBlank
    @Column(nullable = false)
    private String paymentMethod; // e.g., CASH, CREDIT_CARD, etc.

    @NotBlank
    @Column(nullable = false)
    private String status; // e.g., PENDING, PAID, SHIPPED, DELIVERED

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}