package com.example.sachbook.ui.adapter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sachbook.R;
import com.example.sachbook.data.model.OrderModel;
import androidx.navigation.NavController;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderModel> orders = new ArrayList<>();
    private final OnOrderInteractionListener listener;
    private final NavController navController;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long DEBOUNCE_DELAY = 500; // 500ms debounce

    public interface OnOrderInteractionListener {
        void onCancelOrder(OrderModel order);
    }

    public OrderAdapter(OnOrderInteractionListener listener, NavController navController) {
        this.listener = listener;
        this.navController = navController;
    }

    public void updateOrders(List<OrderModel> newOrders) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OrderDiffCallback(orders, newOrders));
        this.orders = newOrders != null ? new ArrayList<>(newOrders) : new ArrayList<>();
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orders.get(position);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        holder.orderIdTextView.setText("Mã đơn hàng: " + order.getId());
        holder.orderStatusTextView.setText("Trạng thái: " + translateStatus(order.getStatus()));
        holder.orderDateTextView.setText("Ngày đặt: " + dateFormat.format(order.getCreatedAt()));
        holder.orderTotalTextView.setText("Tổng tiền: " + currencyFormat.format(order.getFinalAmount()));

        holder.cancelOrderButton.setVisibility(order.getStatus().equals("PENDING") ? View.VISIBLE : View.GONE);
        holder.cancelOrderButton.setEnabled(true);
        holder.cancelOrderButton.setOnClickListener(v -> {
            holder.cancelOrderButton.setEnabled(false);
            handler.postDelayed(() -> holder.cancelOrderButton.setEnabled(true), DEBOUNCE_DELAY);
            listener.onCancelOrder(order);
        });

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("orderId", order.getId());
            navController.navigate(R.id.action_orderHistoryFragment_to_orderDetailFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
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

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, orderStatusTextView, orderDateTextView, orderTotalTextView;
        Button cancelOrderButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdText);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusText);
            orderDateTextView = itemView.findViewById(R.id.orderDateText);
            orderTotalTextView = itemView.findViewById(R.id.orderTotalText);
            cancelOrderButton = itemView.findViewById(R.id.cancelButton);
        }
    }

    static class OrderDiffCallback extends DiffUtil.Callback {
        private final List<OrderModel> oldList;
        private final List<OrderModel> newList;

        OrderDiffCallback(List<OrderModel> oldList, List<OrderModel> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            OrderModel oldOrder = oldList.get(oldItemPosition);
            OrderModel newOrder = newList.get(newItemPosition);
            return oldOrder.getId() == newOrder.getId() &&
                    oldOrder.getStatus().equals(newOrder.getStatus()) &&
                    oldOrder.getFinalAmount() == newOrder.getFinalAmount() &&
                    oldOrder.getCreatedAt().equals(newOrder.getCreatedAt());
        }
    }
}