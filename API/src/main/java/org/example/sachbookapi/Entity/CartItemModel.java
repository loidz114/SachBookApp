package org.example.sachbookapi.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cart_items")
public class CartItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sản phẩm là sách
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
    private Double price;

    // Giỏ hàng chứa item này
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private CartModel cart;
}