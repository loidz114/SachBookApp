package com.example.sachbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.UpdateTotalPriceListener {

    private RecyclerView recyclerViewCart;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        cartItems = new ArrayList<>();
        cartItems.add(new CartItem("1", "Tiểu thuyết 1", 50000, 1));
        cartItems.add(new CartItem("2", "Khoa học 2", 75000, 2));

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartItems, this);
        recyclerViewCart.setAdapter(cartAdapter);

        updateTotalPrice();

        // Xử lý nút Thanh toán
        btnCheckout.setOnClickListener(v -> {
            // Tạo Intent để chuyển sang OrderActivity
            Intent intent = new Intent(getActivity(), OrderActivity.class);
            // Gửi danh sách cartItems
            intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cartItems));
            // Gửi tổng tiền
            double totalPrice = calculateTotalPrice();
            intent.putExtra("totalPrice", totalPrice);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onUpdateTotalPrice() {
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = calculateTotalPrice();
        tvTotalPrice.setText("Tổng tiền: " + total + " VNĐ");
    }

    private double calculateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
}