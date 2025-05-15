package com.example.sachbook.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sachbook.R;
import com.example.sachbook.data.model.CategoryModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private final Context context;
    private List<CategoryModel> categoryList;
    private OnCategoryClickListener onCategoryClickListener;

    // Constructor
    public CategoryAdapter(Context context, List<CategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList != null ? new ArrayList<>(categoryList) : new ArrayList<>();
    }

    // Setter for click listener
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel category = categoryList.get(position);

        // Bind data with null check
        holder.nameTextView.setText(category.getName() != null ? category.getName() : "");

        // Handle click event
        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {
                onCategoryClickListener.onCategoryClick(category, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // Update category list using DiffUtil
    public void updateCategories(List<CategoryModel> newCategoryList) {
        List<CategoryModel> oldList = new ArrayList<>(this.categoryList);
        this.categoryList = newCategoryList != null ? new ArrayList<>(newCategoryList) : new ArrayList<>();

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return categoryList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).getId().equals(categoryList.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                CategoryModel oldCategory = oldList.get(oldItemPosition);
                CategoryModel newCategory = categoryList.get(newItemPosition);
                return oldCategory.getName() != null && oldCategory.getName().equals(newCategory.getName()) &&
                        oldCategory.getDescription() != null && oldCategory.getDescription().equals(newCategory.getDescription());
            }
        });

        diffResult.dispatchUpdatesTo(this);
    }

    // ViewHolder class
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.categoryName);
        }
    }

    // Interface for click listener
    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryModel category, int position);
    }
}