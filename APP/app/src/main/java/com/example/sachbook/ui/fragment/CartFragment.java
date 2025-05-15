package com.example.sachbook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sachbook.R;
import com.example.sachbook.data.model.CartItemModel;
import com.example.sachbook.data.model.CartModel;
import com.example.sachbook.ui.activity.LoginActivity;
import com.example.sachbook.ui.adapter.CartAdapter;
import com.example.sachbook.ui.viewmodel.CartViewModel;
import com.example.sachbook.ui.viewmodel.CartViewModelFactory;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements com.example.sachbook.ui.adapter.CartAdapter.OnCartItemInteractionListener {

    private RecyclerView cartRecyclerView;
    private TextView totalPriceText;
    private MaterialButton checkoutButton;
    private ProgressBar checkoutProgressBar;
    private TextView emptyCartText;
    private CartViewModel cartViewModel;
    private NavController navController;
    private com.example.sachbook.ui.adapter.CartAdapter cartAdapter;
    private static final String PREFS_NAME = "SachBookPrefs";
    private static final String KEY_TOKEN = "auth_token";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        totalPriceText = view.findViewById(R.id.totalPriceText);
        checkoutButton = view.findViewById(R.id.checkoutButton);
        checkoutProgressBar = view.findViewById(R.id.checkoutProgressBar);
        emptyCartText = view.findViewById(R.id.emptyCartText);

        navController = NavHostFragment.findNavController(this);

        CartViewModelFactory factory = new CartViewModelFactory(requireActivity().getApplication());
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        cartAdapter = new CartAdapter(this);
        cartRecyclerView.setAdapter(cartAdapter);

        cartViewModel.getCartState().observe(getViewLifecycleOwner(), state -> {
            checkoutProgressBar.setVisibility(View.GONE);
            checkoutButton.setEnabled(true);
            emptyCartText.setVisibility(View.GONE);
            if (state instanceof CartViewModel.CartState.Loading) {
                checkoutProgressBar.setVisibility(View.VISIBLE);
                checkoutButton.setEnabled(false);
            } else if (state instanceof CartViewModel.CartState.Success) {
                CartModel cart = ((CartViewModel.CartState.Success) state).cart;
                List<CartItemModel> items = (cart != null && cart.getItems() != null) ? cart.getItems() : new ArrayList<>();
                cartAdapter.updateItems(items);
                double total = calculateTotal(items);
                totalPriceText.setText(String.format("%,.0f $", total));
                if (items.isEmpty()) {
                    checkoutButton.setEnabled(false);
                    emptyCartText.setVisibility(View.VISIBLE);
                    emptyCartText.setText("Your cart is empty");
                }
            } else if (state instanceof CartViewModel.CartState.TotalSuccess) {
                double total = ((CartViewModel.CartState.TotalSuccess) state).total;
                totalPriceText.setText(String.format("%,.0f $", total));
            } else if (state instanceof CartViewModel.CartState.Error) {
                emptyCartText.setVisibility(View.VISIBLE);
                emptyCartText.setText(((CartViewModel.CartState.Error) state).message);
                if (((CartViewModel.CartState.Error) state).message.contains("Phiên đăng nhập")) {
                    emptyCartText.setText("Please log in to view your cart");
                }
            }
        });

        checkoutButton.setOnClickListener(v -> {
            String token = getToken();
            if (token == null) {
                Toast.makeText(requireContext(), "Please log in to checkout", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), LoginActivity.class));
                return;
            }
            navController.navigate(R.id.action_cart_to_checkout);
        });

        loadCart();

        return view;
    }

    private void loadCart() {
        String token = getToken();
        if (token == null) {
            cartAdapter.updateItems(new ArrayList<>());
            totalPriceText.setText(String.format("%,.0f $", 0.0));
            checkoutButton.setEnabled(false);
            emptyCartText.setVisibility(View.VISIBLE);
            emptyCartText.setText("Please log in to view your cart");
        } else {
            cartViewModel.getCart(token);
        }
    }

    private String getToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    private double calculateTotal(List<CartItemModel> items) {
        double total = 0;
        if (items != null) {
            for (CartItemModel item : items) {
                if (item != null) {
                    total += (item.getPrice() != null ? item.getPrice() : 0) * (item.getQuantity() != null ? item.getQuantity() : 0);
                }
            }
        }
        return total;
    }

    @Override
    public void onIncreaseQuantity(CartItemModel item) {
        String token = getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Please log in to update cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            return;
        }
        cartViewModel.updateCartItem(token, item.getId(), item.getQuantity() + 1);
    }

    @Override
    public void onDecreaseQuantity(CartItemModel item) {
        String token = getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Please log in to update cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            return;
        }
        if (item.getQuantity() > 1) {
            cartViewModel.updateCartItem(token, item.getId(), item.getQuantity() - 1);
        } else {
            cartViewModel.removeFromCart(token, item.getId());
        }
    }

    @Override
    public void onDeleteItem(CartItemModel item) {
        String token = getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Please log in to remove item from cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            return;
        }
        cartViewModel.removeFromCart(token, item.getId());
    }
}