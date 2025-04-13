package com.example.sachbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Model.Order;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Order> orderList;

    public HistoryAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Hiển thị thông tin đơn hàng
        holder.tvOrderId.setText("Mã đơn hàng: " + order.getOrderId());
        holder.tvFullName.setText("Người nhận: " + order.getFullName());
        holder.tvTotalPrice.setText("Tổng tiền: " + order.getTotalPrice() + " VNĐ");
        holder.tvPaymentMethod.setText("Phương thức thanh toán: " + order.getPaymentMethod());

        // Định dạng ngày đặt hàng
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String orderDate = dateFormat.format(new Date(order.getOrderId()));
        holder.tvOrderDate.setText("Ngày đặt: " + orderDate);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvFullName, tvTotalPrice, tvPaymentMethod;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
        }
    }

    // Cập nhật dữ liệu
    public void updateData(List<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }
}