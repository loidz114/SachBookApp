package com.example.sachbook.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sachbook.R;
import com.example.sachbook.data.model.ReviewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private final List<ReviewModel> reviews;
    private final Long currentUserId;
    private final OnReviewActionListener listener;

    public interface OnReviewActionListener {
        void onEditReview(ReviewModel review);
        void onDeleteReview(ReviewModel review);
    }

    public ReviewAdapter(Context context, Long currentUserId, OnReviewActionListener listener) {
        this.context = context;
        this.reviews = new ArrayList<>();
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    public void updateReviews(List<ReviewModel> newReviews) {
        reviews.clear();
        reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel review = reviews.get(position);
        holder.usernameText.setText(review.getUser().getUsername() != null ? review.getUser().getUsername() : "Anonymous");
        holder.ratingBar.setRating(review.getRating() != null ? review.getRating() : 0);
        holder.commentText.setText(review.getComment() != null ? review.getComment() : "");
        holder.dateText.setText(review.getCreatedAt() != null ?
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(review.getCreatedAt()) : "");

        // Show edit/delete buttons only for current user's review
        boolean isCurrentUser = currentUserId != null && review.getUser() != null && currentUserId.equals(review.getUser().getId());
        holder.editButton.setVisibility(isCurrentUser ? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility(isCurrentUser ? View.VISIBLE : View.GONE);

        holder.editButton.setOnClickListener(v -> listener.onEditReview(review));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteReview(review));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, commentText, dateText;
        RatingBar ratingBar;
        ImageButton editButton, deleteButton;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            commentText = itemView.findViewById(R.id.commentText);
            dateText = itemView.findViewById(R.id.dateText);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}