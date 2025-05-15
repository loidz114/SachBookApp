package com.example.sachbook.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sachbook.R;
import com.example.sachbook.data.model.BookModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private final Context context;
    private List<BookModel> bookList;
    private OnBookClickListener onBookClickListener;
    private Picasso picasso; // Custom Picasso instance with OkHttp

    public BookAdapter(Context context, List<BookModel> bookList) {
        this.context = context;
        this.bookList = bookList != null ? new ArrayList<>(bookList) : new ArrayList<>();

        // Configure OkHttpClient for Picasso with retries and timeouts
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Increase connection timeout
                .readTimeout(30, TimeUnit.SECONDS)    // Increase read timeout
                .writeTimeout(30, TimeUnit.SECONDS)   // Increase write timeout
                .retryOnConnectionFailure(true)       // Enable retries on failure
                .build();

        // Initialize Picasso with OkHttp downloader
        this.picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(okHttpClient))
                .build();
    }

    public void setOnBookClickListener(OnBookClickListener listener) {
        this.onBookClickListener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookModel book = bookList.get(position);

        // Bind text data
        holder.titleTextView.setText(book.getTitle() != null ? book.getTitle() : "Unknown Title");
        holder.authorTextView.setText(book.getAuthor() != null ? book.getAuthor() : "Unknown Author");
        holder.priceTextView.setText(book.getPrice() != null ? String.format("$%.2f", book.getPrice()) : "N/A");

        // Load image with Picasso
        if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
            Log.d("PicassoDebug", "Attempting to load image from URL: " + book.getImageUrl());
            picasso.load(book.getImageUrl())
                    .placeholder(R.drawable.placeholder_book) // Show placeholder while loading
                    .error(R.drawable.placeholder_book)      // Show placeholder on error
                    .into(holder.bookImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("PicassoDebug", "Image loaded successfully for: " + book.getTitle());
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("PicassoDebug", "Failed to load image for " + book.getTitle() + ": " + e.getMessage());
                            holder.bookImageView.setImageResource(R.drawable.placeholder_book);
                        }
                    });
        } else {
            Log.d("PicassoDebug", "Image URL is null or empty for: " + book.getTitle());
            holder.bookImageView.setImageResource(R.drawable.placeholder_book);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (onBookClickListener != null) {
                onBookClickListener.onBookClick(book, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void updateBooks(List<BookModel> newBookList) {
        List<BookModel> oldList = new ArrayList<>(this.bookList);
        this.bookList = newBookList != null ? new ArrayList<>(newBookList) : new ArrayList<>();
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return bookList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).getId().equals(bookList.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                BookModel oldBook = oldList.get(oldItemPosition);
                BookModel newBook = bookList.get(newItemPosition);
                return oldBook.getTitle() != null && oldBook.getTitle().equals(newBook.getTitle()) &&
                        oldBook.getAuthor() != null && oldBook.getAuthor().equals(newBook.getAuthor()) &&
                        oldBook.getPrice() != null && oldBook.getPrice().equals(newBook.getPrice()) &&
                        oldBook.getImageUrl() != null && oldBook.getImageUrl().equals(newBook.getImageUrl());
            }
        });
        diffResult.dispatchUpdatesTo(this);
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImageView;
        TextView titleTextView, authorTextView, priceTextView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImageView = itemView.findViewById(R.id.book_image);
            titleTextView = itemView.findViewById(R.id.book_title);
            authorTextView = itemView.findViewById(R.id.book_author);
            priceTextView = itemView.findViewById(R.id.book_price);
        }
    }

    public interface OnBookClickListener {
        void onBookClick(BookModel book, int position);
    }
}