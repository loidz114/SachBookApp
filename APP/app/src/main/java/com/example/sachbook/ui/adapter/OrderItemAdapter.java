package com.example.sachbook.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sachbook.R;
import com.example.sachbook.data.model.OrderItemModel;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<OrderItemModel> orderItems = new ArrayList<>();

    public void updateOrderItems(List<OrderItemModel> newOrderItems) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new OrderItemDiffCallback(orderItems, newOrderItems));
        this.orderItems = newOrderItems != null ? new ArrayList<>(newOrderItems) : new ArrayList<>();
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItemModel item = orderItems.get(position);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        holder.bookTitleTextView.setText("Mã sách: " + (item.getBookId() != null ? item.getBookId() : "N/A"));
        holder.quantityTextView.setText("Số lượng: " + item.getQuantity());
        holder.unitPriceTextView.setText("Đơn giá: " + currencyFormat.format(item.getUnitPrice()));
        holder.totalPriceTextView.setText("Tổng giá: " + currencyFormat.format(item.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitleTextView, quantityTextView, unitPriceTextView, totalPriceTextView;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitleTextView = itemView.findViewById(R.id.bookTitleTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            unitPriceTextView = itemView.findViewById(R.id.unitPriceTextView);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
        }
    }

    static class OrderItemDiffCallback extends DiffUtil.Callback {
        private final List<OrderItemModel> oldList;
        private final List<OrderItemModel> newList;

        OrderItemDiffCallback(List<OrderItemModel> oldList, List<OrderItemModel> newList) {
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
            return oldList.get(oldItemPosition).getBookId().equals(newList.get(newItemPosition).getBookId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            OrderItemModel oldItem = oldList.get(oldItemPosition);
            OrderItemModel newItem = newList.get(newItemPosition);
            return oldItem.getBookId().equals(newItem.getBookId()) &&
                    oldItem.getQuantity() == newItem.getQuantity() &&
                    oldItem.getUnitPrice() == newItem.getUnitPrice() &&
                    oldItem.getTotalPrice() == newItem.getTotalPrice();
        }
    }
}