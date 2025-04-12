package org.example.sachbookapi.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "order_items")
public class OrderItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sách trong đơn hàng
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private BookModel book;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double unitPrice;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double totalPrice;

    // Đơn hàng cha
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderModel order;
}