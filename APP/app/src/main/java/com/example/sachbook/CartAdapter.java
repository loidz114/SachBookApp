package com.example.sachbook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private UpdateTotalPriceListener listener;
    private boolean readOnly = false;

    public CartAdapter(List<CartItem> cartItems, UpdateTotalPriceListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.tvBookTitle.setText(cartItem.getBookTitle());
        holder.tvPrice.setText(cartItem.getPrice() + " VNĐ");
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        if (readOnly) {
            // Ẩn các nút tăng/giảm số lượng
            holder.btnIncrease.setVisibility(View.GONE);
            holder.btnDecrease.setVisibility(View.GONE);
        } else {
            // Hiển thị các nút và xử lý sự kiện
            holder.btnIncrease.setVisibility(View.VISIBLE);
            holder.btnDecrease.setVisibility(View.VISIBLE);

            holder.btnIncrease.setOnClickListener(v -> {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                if (listener != null) listener.onUpdateTotalPrice();
            });

            holder.btnDecrease.setOnClickListener(v -> {
                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                    if (listener != null) listener.onUpdateTotalPrice();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle, tvPrice, tvQuantity;
        Button btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }

    public interface UpdateTotalPriceListener {
        void onUpdateTotalPrice();
    }
}