package org.example.sachbookapi.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "carts")
public class CartModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mỗi user chỉ có 1 giỏ hàng
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserModel user;

    // Danh sách sản phẩm trong giỏ
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemModel> items;
}
