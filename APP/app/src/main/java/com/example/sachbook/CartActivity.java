package com.example.sachbook; // Thay bằng package của bạn

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private List<com.example.sachbook.CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Ánh xạ các thành phần giao diện
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);

        // Khởi tạo danh sách giỏ hàng (tạm thời)
        cartItems = new ArrayList<>();
        // Ví dụ: Thêm dữ liệu giả để kiểm tra
        cartItems.add(new com.example.sachbook.CartItem("1", "Tiểu thuyết 1", 50000, 1));
        cartItems.add(new com.example.sachbook.CartItem("2", "Khoa học 2", 75000, 2));

        // Thiết lập RecyclerView
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems, this::updateTotalPrice);
        recyclerViewCart.setAdapter(cartAdapter);

        // Cập nhật tổng tiền ban đầu
        updateTotalPrice();

        // Xử lý nút thanh toán
        btnCheckout.setOnClickListener(v -> {
            // Thêm logic thanh toán ở đây (ví dụ: chuyển sang màn hình thanh toán)
        });
    }

    // Hàm cập nhật tổng tiền
    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        tvTotalPrice.setText("Tổng tiền: " + total + " VNĐ");
    }
}