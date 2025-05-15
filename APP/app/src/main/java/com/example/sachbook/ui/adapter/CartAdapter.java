package com.example.sachbook.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sachbook.R;
import com.example.sachbook.data.model.CartItemModel;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItemModel> cartItems;
    private final OnCartItemInteractionListener listener;

    public interface OnCartItemInteractionListener {
        void onIncreaseQuantity(CartItemModel item);
        void onDecreaseQuantity(CartItemModel item);
        void onDeleteItem(CartItemModel item);
    }

    public CartAdapter(OnCartItemInteractionListener listener) {
        this.cartItems = new ArrayList<>();
        this.listener = listener;
    }

    public void updateItems(List<CartItemModel> newItems) {
        this.cartItems = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemModel item = cartItems.get(position);

        holder.bookTitle.setText(item.getBook().getTitle() != null ? item.getBook().getTitle() : "Unknown Title");
        holder.bookAuthor.setText(item.getBook().getAuthor() != null ? "Tác giả: " + item.getBook().getAuthor() : "Unknown Author");
        holder.bookPrice.setText(String.format("%,.0f VNĐ", item.getPrice() * item.getQuantity()));
        holder.bookQuantity.setText(String.valueOf(item.getQuantity()));

        if (item.getBook().getImageUrl() != null && !item.getBook().getImageUrl().isEmpty()) {
            Picasso.get().load(item.getBook().getImageUrl()).into(holder.bookImage);
        } else {
            holder.bookImage.setImageResource(R.drawable.placeholder_book);
        }

        holder.increaseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIncreaseQuantity(item);
            }
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDecreaseQuantity(item);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteItem(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookTitle, bookAuthor, bookPrice, bookQuantity;
        ImageButton increaseButton, decreaseButton, deleteButton;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            bookPrice = itemView.findViewById(R.id.bookPrice);
            bookQuantity = itemView.findViewById(R.id.bookQuantity);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}