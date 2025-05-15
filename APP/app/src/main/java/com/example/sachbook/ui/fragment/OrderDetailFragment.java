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
import com.example.sachbook.data.model.OrderModel;
import com.example.sachbook.ui.activity.LoginActivity;
import com.example.sachbook.ui.adapter.OrderItemAdapter;
import com.example.sachbook.ui.viewmodel.CartViewModel;
import com.example.sachbook.ui.viewmodel.CartViewModelFactory;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderDetailFragment extends Fragment {

    private TextView orderIdTextView, statusTextView, totalAmountTextView, createdAtTextView;
    private RecyclerView orderItemsRecyclerView;
    private ProgressBar progressBar;
    private NavController navController;
    private CartViewModel cartViewModel;
    private OrderItemAdapter orderItemAdapter;
    private static final String PREFS_NAME = "SachBookPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private long orderId;
    private boolean isLoading = false;
    private View rootView; // Added to store the inflated view

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_detail, container, false); // Store in rootView

        // Initialize UI
        orderIdTextView = rootView.findViewById(R.id.orderIdText);
        statusTextView = rootView.findViewById(R.id.orderStatusText);
        totalAmountTextView = rootView.findViewById(R.id.orderTotalText);
        createdAtTextView = rootView.findViewById(R.id.orderDateText);
        orderItemsRecyclerView = rootView.findViewById(R.id.orderItemsRecyclerView);
        progressBar = rootView.findViewById(R.id.progressBar);

        // Setup navigation
        navController = NavHostFragment.findNavController(this);

        // Initialize ViewModel
        CartViewModelFactory factory = new CartViewModelFactory(requireActivity().getApplication());
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);

        // Setup RecyclerView
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderItemAdapter = new OrderItemAdapter();
        orderItemsRecyclerView.setAdapter(orderItemAdapter);

        // Get orderId from arguments
        if (getArguments() != null) {
            orderId = getArguments().getLong("orderId", -1);
            if (orderId == -1) {
                Toast.makeText(requireContext(), "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
                navController.popBackStack();
                return rootView;
            }
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            navController.popBackStack();
            return rootView;
        }

        // Observe ViewModel state
        cartViewModel.getCartState().observe(getViewLifecycleOwner(), state -> {
            if (!isAdded()) return;

            progressBar.setVisibility(View.GONE);
            orderItemsRecyclerView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.orderInfoCard).setVisibility(View.VISIBLE);

            if (state instanceof CartViewModel.CartState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                orderItemsRecyclerView.setVisibility(View.GONE);
                rootView.findViewById(R.id.orderInfoCard).setVisibility(View.GONE);
                isLoading = true;
            } else if (state instanceof CartViewModel.CartState.OrderSuccess) {
                isLoading = false;
                OrderModel order = ((CartViewModel.CartState.OrderSuccess) state).order;
                if (order == null || order.getId() != orderId) {
                    showError("Đơn hàng không hợp lệ");
                    return;
                }
                displayOrderDetails(order);
                Toast.makeText(requireContext(), "Tải chi tiết đơn hàng thành công", Toast.LENGTH_SHORT).show();
            } else if (state instanceof CartViewModel.CartState.Error) {
                isLoading = false;
                String errorMessage = ((CartViewModel.CartState.Error) state).message;
                showError(errorMessage);
                if (errorMessage.contains("Phiên đăng nhập")) {
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                }
            }
        });

        // Load order details
        loadOrderDetails();

        return rootView;
    }

    private void loadOrderDetails() {
        if (isLoading) return;
        String token = getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem chi tiết đơn hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
            return;
        }
        cartViewModel.getUserOrderById(token, orderId);
    }

    private String getToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    private void displayOrderDetails(OrderModel order) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        orderIdTextView.setText("Mã đơn hàng: " + order.getId());
        statusTextView.setText("Trạng thái: " + translateStatus(order.getStatus()));
        totalAmountTextView.setText("Tổng tiền: " + currencyFormat.format(order.getFinalAmount()));
        createdAtTextView.setText("Ngày đặt: " + dateFormat.format(order.getCreatedAt()));

        orderItemAdapter.updateOrderItems(order.getOrderItems());
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        orderItemsRecyclerView.setVisibility(View.GONE);
        rootView.findViewById(R.id.orderInfoCard).setVisibility(View.GONE);
    }

    private String translateStatus(String status) {
        if (status == null) return "Không xác định";
        switch (status) {
            case "PENDING": return "Đang chờ xử lý";
            case "PAID": return "Đã thanh toán";
            case "SHIPPED": return "Đã giao hàng";
            case "DELIVERED": return "Đã nhận hàng";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }
}