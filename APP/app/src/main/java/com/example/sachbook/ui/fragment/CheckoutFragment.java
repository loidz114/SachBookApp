package com.example.sachbook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.sachbook.R;
import com.example.sachbook.data.model.CartModel;
import com.example.sachbook.data.model.OrderRequest;
import com.example.sachbook.ui.activity.LoginActivity;
import com.example.sachbook.ui.viewmodel.CartViewModel;
import com.example.sachbook.ui.viewmodel.CartViewModelFactory;

public class CheckoutFragment extends Fragment {

    private TextView checkoutTotal;
    private Spinner paymentMethodSpinner;
    private EditText discountCodeEditText, shippingAddressEditText;
    private Button confirmOrderButton;
    private ProgressBar checkoutProgressBar;
    private ImageButton backButton;
    private NavController navController;
    private CartViewModel cartViewModel;
    private static final String PREFS_NAME = "SachBookPrefs";
    private static final String KEY_TOKEN = "auth_token";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        // Initialize views
        checkoutTotal = view.findViewById(R.id.checkoutTotal);
        paymentMethodSpinner = view.findViewById(R.id.paymentMethodSpinner);
        discountCodeEditText = view.findViewById(R.id.discountCodeEditText);
        shippingAddressEditText = view.findViewById(R.id.shippingAddressEditText);
        confirmOrderButton = view.findViewById(R.id.confirmOrderButton);
        checkoutProgressBar = view.findViewById(R.id.checkoutProgressBar);
        backButton = view.findViewById(R.id.backButton);

        // Initialize navigation and ViewModel
        navController = NavHostFragment.findNavController(this);
        CartViewModelFactory factory = new CartViewModelFactory(requireActivity().getApplication());
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);

        // Set up payment method spinner
        String[] paymentMethods = {"CASH", "MOMO", "VNPAY"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, paymentMethods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentMethodSpinner.setAdapter(adapter);

        // Update total when discount code changes
        discountCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                applyDiscount(s.toString().trim());
            }
        });

        // Handle back button click
        backButton.setOnClickListener(v -> navController.navigate(R.id.action_checkout_to_cart));

        // Observe cart state
        cartViewModel.getCartState().observe(getViewLifecycleOwner(), state -> {
            checkoutProgressBar.setVisibility(View.GONE);
            confirmOrderButton.setEnabled(false);
            if (state instanceof CartViewModel.CartState.Loading) {
                checkoutProgressBar.setVisibility(View.VISIBLE);
            } else if (state instanceof CartViewModel.CartState.Success) {
                CartModel cart = ((CartViewModel.CartState.Success) state).cart;
                Log.d("CheckoutFragment", "Cart items: " + (cart.getItems() != null ? cart.getItems().toString() : "null"));
                if (cart.getItems() == null || cart.getItems().isEmpty()) {
                    checkoutTotal.setText("$0.00");
                    Toast.makeText(requireContext(), "Giỏ hàng trống, vui lòng thêm sản phẩm", Toast.LENGTH_SHORT).show();
                } else {
                    String token = getToken();
                    if (token != null) {
                        String discount = discountCodeEditText.getText().toString().trim();
                        cartViewModel.getCartTotal(token, discount.isEmpty() ? null : discount);
                    } else {
                        Toast.makeText(requireContext(), "Bạn hãy đăng nhập", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(requireContext(), LoginActivity.class));
                    }
                }
            } else if (state instanceof CartViewModel.CartState.TotalSuccess) {
                double totalInUSD = ((CartViewModel.CartState.TotalSuccess) state).total;
                Log.d("CheckoutFragment", "Total received: " + totalInUSD);
                checkoutTotal.setText(String.format("$%.2f", totalInUSD));
                confirmOrderButton.setEnabled(totalInUSD > 0);
            } else if (state instanceof CartViewModel.CartState.OrderSuccess) {
                String paymentMethod = ((CartViewModel.CartState.OrderSuccess) state).order.getPaymentMethod();
                String message = paymentMethod.equalsIgnoreCase("CASH")
                        ? "Đặt hàng thành công! Vui lòng chuẩn bị tiền mặt khi nhận hàng."
                        : "Thanh toán thành công! Đơn hàng đã được đặt.";
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                navController.navigate(R.id.action_checkout_to_order_history);
            } else if (state instanceof CartViewModel.CartState.Error) {
                String message = ((CartViewModel.CartState.Error) state).message;
                Log.e("CheckoutFragment", "Error: " + message);
                if (message.contains("Phiên đăng nhập")) {
                    Toast.makeText(requireContext(), "Bạn hãy đăng nhập", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                } else {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                }
                checkoutTotal.setText("$0.00");
            }
        });

        // Handle confirm order
        confirmOrderButton.setOnClickListener(v -> {
            String token = getToken();
            if (token == null) {
                Toast.makeText(requireContext(), "Bạn hãy đăng nhập", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), LoginActivity.class));
                return;
            }

            if (paymentMethodSpinner.getSelectedItem() == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            String shippingAddress = shippingAddressEditText.getText().toString().trim();
            if (shippingAddress.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create order request
            String paymentMethod = paymentMethodSpinner.getSelectedItem().toString();
            String discount = discountCodeEditText.getText().toString().trim();
            OrderRequest request = new OrderRequest();
            try {
                request.setPaymentMethod(paymentMethod);
                request.setShippingAddress(shippingAddress);
                request.setDiscountCode(discount.isEmpty() ? null : discount);
            } catch (IllegalArgumentException e) {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("CheckoutFragment", "OrderRequest: paymentMethod=" + request.getPaymentMethod() +
                    ", shippingAddress=" + request.getShippingAddress() +
                    ", discountCode=" + request.getDiscountCode());
            checkoutProgressBar.setVisibility(View.VISIBLE);
            confirmOrderButton.setEnabled(false);
            cartViewModel.createOrder(token, request);
        });

        // Initial load
        loadCart();
        Bundle args = getArguments();
        if (args != null && args.getBoolean("refreshTotal", false)) {
            String token = getToken();
            if (token != null) {
                cartViewModel.getCartTotal(token, null);
            } else {
                Toast.makeText(requireContext(), "Bạn hãy đăng nhập", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(requireContext(), LoginActivity.class));
            }
        }

        return view;
    }

    private void loadCart() {
        String token = getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Bạn hãy đăng nhập", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            return;
        }
        cartViewModel.getCart(token);
    }

    private void applyDiscount(String discount) {
        String token = getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Bạn hãy đăng nhập", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            return;
        }
        cartViewModel.getCartTotal(token, discount.isEmpty() ? null : discount);
    }

    private String getToken() {
        Context context = getContext();
        if (context == null) {
            return null;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }
}