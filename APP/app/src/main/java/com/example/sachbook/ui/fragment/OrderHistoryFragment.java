package com.example.sachbook.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.example.sachbook.ui.adapter.OrderAdapter;
import com.example.sachbook.ui.viewmodel.CartViewModel;
import com.example.sachbook.ui.viewmodel.CartViewModelFactory;
import java.util.ArrayList;
import java.util.List;

public class OrderHistoryFragment extends Fragment implements OrderAdapter.OnOrderInteractionListener {

    private RecyclerView orderRecyclerView;
    private ProgressBar progressBar;
    private NavController navController;
    private CartViewModel cartViewModel;
    private OrderAdapter orderAdapter;
    private static final String PREFS_NAME = "SachBookPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        // Initialize UI
        orderRecyclerView = view.findViewById(R.id.orderRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);

        // Setup navigation
        navController = NavHostFragment.findNavController(this);

        // Initialize ViewModel
        CartViewModelFactory factory = new CartViewModelFactory(requireActivity().getApplication());
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);

        // Setup RecyclerView
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        orderAdapter = new OrderAdapter(this, navController);
        orderRecyclerView.setAdapter(orderAdapter);

        // Observe ViewModel state
        cartViewModel.getCartState().observe(getViewLifecycleOwner(), state -> {
            if (!isAdded()) return;

            progressBar.setVisibility(View.GONE);
            orderRecyclerView.setVisibility(View.VISIBLE);

            if (state instanceof CartViewModel.CartState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                orderRecyclerView.setVisibility(View.GONE);
                isLoading = true;
            } else if (state instanceof CartViewModel.CartState.OrderHistorySuccess) {
                isLoading = false;
                List<OrderModel> orders = ((CartViewModel.CartState.OrderHistorySuccess) state).orders;
                if (orders == null || orders.isEmpty()) {
                    Toast.makeText(requireContext(), "Không có đơn hàng nào", Toast.LENGTH_SHORT).show();
                    orderAdapter.updateOrders(new ArrayList<>());
                } else {
                    orderAdapter.updateOrders(orders);
                    Toast.makeText(requireContext(), "Tải lịch sử đơn hàng thành công", Toast.LENGTH_SHORT).show();
                }
            } else if (state instanceof CartViewModel.CartState.OrderSuccess) {
                isLoading = false;
                Toast.makeText(requireContext(), "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                loadOrders();
            } else if (state instanceof CartViewModel.CartState.Error) {
                isLoading = false;
                String errorMessage = ((CartViewModel.CartState.Error) state).message;
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                if (errorMessage.contains("Phiên đăng nhập")) {
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                } else if (errorMessage.contains("Network")) {
                    Toast.makeText(requireContext(), "Kiểm tra kết nối mạng", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadOrders();
        return view;
    }

    private void loadOrders() {
        if (isLoading) return;
        String token = getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để xem lịch sử đơn hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
            return;
        }
        cartViewModel.getUserOrders(token);
    }

    private String getToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    @Override
    public void onCancelOrder(OrderModel order) {
        if (isLoading) return;
        String token = getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để hủy đơn hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
            return;
        }
        if (!order.getStatus().equals("PENDING")) {
            Toast.makeText(requireContext(), "Đơn hàng không thể hủy", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setMessage("Bạn có chắc muốn hủy đơn hàng này?")
                .setPositiveButton("Hủy", (dialog, which) -> {
                    isLoading = true;
                    cartViewModel.cancelOrder(token, order.getId());
                })
                .setNegativeButton("Không", null)
                .show();
    }
}