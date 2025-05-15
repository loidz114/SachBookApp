package com.example.sachbook.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sachbook.R;
import com.example.sachbook.data.model.BookModel;
import com.example.sachbook.data.model.ReviewModel;
import com.example.sachbook.data.model.ReviewRequest;
import com.example.sachbook.data.model.UserModel;
import com.example.sachbook.data.repository.BookRepository;
import com.example.sachbook.ui.activity.LoginActivity;
import com.example.sachbook.ui.adapter.ReviewAdapter;
import com.example.sachbook.ui.viewmodel.CartViewModel;
import com.example.sachbook.ui.viewmodel.CartViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailFragment extends Fragment implements ReviewAdapter.OnReviewActionListener {

    private ImageButton backButton;
    private ImageView bookImage;
    private TextView bookTitle, bookAuthor, bookPrice, bookDescription;
    private RatingBar bookRatingBar;
    private RecyclerView reviewRecyclerView;
    private MaterialButton addToCartButton, buyNowButton, addReviewButton;
    private BookRepository repository;
    private NavController navController;
    private CartViewModel cartViewModel;
    private long bookId;
    private String bookTitleArg;
    private Long userId;
    private Toast currentToast;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private long lastToastTime = 0;
    private static final long TOAST_DEBOUNCE_MS = 2000;
    private static final String PREFS_NAME = "SachBookPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private ActivityResultLauncher<Intent> loginLauncher;
    private boolean isBuyNowPending;
    private boolean isCartCleared;
    private ReviewAdapter reviewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        // Initialize repository
        repository = new BookRepository(requireContext());

        // Initialize ViewModel
        CartViewModelFactory factory = new CartViewModelFactory(requireActivity().getApplication());
        cartViewModel = new ViewModelProvider(this, factory).get(CartViewModel.class);

        // Bind views
        backButton = view.findViewById(R.id.backButton);
        bookImage = view.findViewById(R.id.bookImage);
        bookTitle = view.findViewById(R.id.bookTitle);
        bookAuthor = view.findViewById(R.id.bookAuthor);
        bookPrice = view.findViewById(R.id.bookPrice);
        bookRatingBar = view.findViewById(R.id.bookRatingBar);
        bookDescription = view.findViewById(R.id.bookDescription);
        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        addToCartButton = view.findViewById(R.id.addToCartButton);
        buyNowButton = view.findViewById(R.id.buyNowButton);
        addReviewButton = view.findViewById(R.id.addReviewButton);

        // Set up NavController
        navController = NavHostFragment.findNavController(this);

        // Restore state
        if (savedInstanceState != null) {
            isBuyNowPending = savedInstanceState.getBoolean("isBuyNowPending", false);
            isCartCleared = savedInstanceState.getBoolean("isCartCleared", false);
            bookId = savedInstanceState.getLong("bookId", 0);
            bookTitleArg = savedInstanceState.getString("bookTitleArg");
        }

        // Get arguments
        if (getArguments() != null) {
            bookId = getArguments().getLong("bookId");
            bookTitleArg = getArguments().getString("bookTitle");
        }

        // Validate bookId
        if (bookId <= 0) {
            showToast("ID sách không hợp lệ");
            navController.navigateUp();
            return view;
        }

        // Set up RecyclerView for reviews
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewAdapter = new ReviewAdapter(requireContext(), userId, this);
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewRecyclerView.setVisibility(View.VISIBLE);

        // Set up ActivityResultLauncher for LoginActivity
        loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                String token = getToken();
                if (token != null) {
                    loadUserProfile(token);
                    if (isBuyNowPending) {
                        handleBuyNow(token);
                    } else {
                        cartViewModel.addToCart(token, bookId, 1);
                    }
                } else {
                    showToast("Không tìm thấy token, vui lòng thử lại");
                }
            } else {
                showToast("Đăng nhập bị hủy");
                isBuyNowPending = false;
                isCartCleared = false;
            }
        });

        // Observe cart state
        cartViewModel.getCartState().observe(getViewLifecycleOwner(), state -> {
            addToCartButton.setEnabled(true);
            buyNowButton.setEnabled(true);
            if (state instanceof CartViewModel.CartState.Loading) {
                addToCartButton.setEnabled(false);
                buyNowButton.setEnabled(false);
                showToast(isBuyNowPending ? "Đang chuẩn bị thanh toán..." : "Đang thêm vào giỏ hàng...");
            } else if (state instanceof CartViewModel.CartState.Success) {
                String message = ((CartViewModel.CartState.Success) state).message;
                showToast(message);
                if (isBuyNowPending) {
                    String token = getToken();
                    if (token != null) {
                        if (!isCartCleared) {
                            // Cart cleared successfully, now add the book
                            isCartCleared = true;
                            cartViewModel.addToCart(token, bookId, 1);
                        } else {
                            // Book added successfully, navigate to checkout
                            Bundle args = new Bundle();
                            args.putBoolean("refreshTotal", true);
                            navController.navigate(R.id.action_book_detail_to_checkout, args);
                            isBuyNowPending = false;
                            isCartCleared = false;
                        }
                    }
                }
            } else if (state instanceof CartViewModel.CartState.Error) {
                showToast(((CartViewModel.CartState.Error) state).message);
                if (((CartViewModel.CartState.Error) state).message.contains("Phiên đăng nhập")) {
                    navigateToLogin();
                }
                isBuyNowPending = false;
                isCartCleared = false;
            }
        });

        // Handle back button
        backButton.setOnClickListener(v -> navController.navigateUp());

        // Handle add to cart
        addToCartButton.setOnClickListener(v -> {
            String token = getToken();
            if (token == null) {
                showToast("Vui lòng đăng nhập để thêm vào giỏ hàng");
                isBuyNowPending = false;
                navigateToLogin();
                return;
            }
            cartViewModel.addToCart(token, bookId, 1);
        });

        // Handle buy now
        buyNowButton.setOnClickListener(v -> {
            String token = getToken();
            if (token == null) {
                showToast("Vui lòng đăng nhập để mua ngay");
                isBuyNowPending = true;
                navigateToLogin();
                return;
            }
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận")
                    .setMessage("Hành động này sẽ xóa giỏ hàng hiện tại và chỉ mua 1 cuốn sách này. Bạn có muốn tiếp tục?")
                    .setPositiveButton("Có", (dialog, which) -> handleBuyNow(token))
                    .setNegativeButton("Không", null)
                    .show();
        });

        // Handle add review
        addReviewButton.setOnClickListener(v -> {
            String token = getToken();
            if (token == null) {
                showToast("Vui lòng đăng nhập để đánh giá");
                navigateToLogin();
                return;
            }
            if (userId == null) {
                loadUserProfile(token);
                showToast("Đang tải thông tin người dùng...");
                return;
            }
            showReviewDialog(null);
        });

        // Load book details and reviews
        loadBookDetails();
        loadReviews();

        // Load user profile if logged in
        String token = getToken();
        if (token != null) {
            loadUserProfile(token);
        }

        return view;
    }

    private void handleBuyNow(String token) {
        isBuyNowPending = true;
        isCartCleared = false;
        cartViewModel.clearCart(token);
    }

    private void loadBookDetails() {
        Call<BookModel> call = repository.getBookById(bookId);
        call.enqueue(new Callback<BookModel>() {
            @Override
            public void onResponse(Call<BookModel> call, Response<BookModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BookModel book = response.body();
                    Log.d("BookDetailFragment", "Book Response: " + book.toString());
                    bookTitle.setText(book.getTitle() != null ? book.getTitle() : "Không có tiêu đề");
                    bookAuthor.setText(book.getAuthor() != null ? "Tác giả: " + book.getAuthor() : "Không có tác giả");
                    Double priceInUSD = book.getPrice();
                    if (priceInUSD != null && priceInUSD > 0) {
                        bookPrice.setText(String.format("$%.2f", priceInUSD));
                    } else {
                        bookPrice.setText("Price not available");
                    }
                    bookDescription.setText(book.getDescription() != null ? book.getDescription() : "Không có mô tả");
                    if (book.getImageUrl() != null && !book.getImageUrl().isEmpty()) {
                        Picasso.get().load(book.getImageUrl()).into(bookImage);
                    } else {
                        bookImage.setImageResource(R.drawable.placeholder_book);
                    }
                } else {
                    showToast("Không thể tải chi tiết sách. Mã lỗi: " + response.code());
                    bookPrice.setText("Price not available");
                }
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                showToast("Lỗi mạng: " + t.getMessage());
                bookPrice.setText("Price not available");
            }
        });
    }

    private void loadUserProfile(String token) {
        Call<UserModel> call = repository.getUserProfile(token);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userId = response.body().getId();
                    reviewAdapter = new ReviewAdapter(requireContext(), userId, BookDetailFragment.this);
                    reviewRecyclerView.setAdapter(reviewAdapter);
                    loadReviews();
                } else {
                    showToast("Không thể tải thông tin người dùng");
                    userId = null;
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                showToast("Lỗi mạng: " + t.getMessage());
                userId = null;
            }
        });
    }

    private void loadReviews() {
        Call<List<ReviewModel>> call = repository.getReviewsByBookId(bookId);
        call.enqueue(new Callback<List<ReviewModel>>() {
            @Override
            public void onResponse(Call<List<ReviewModel>> call, Response<List<ReviewModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReviewModel> reviews = response.body();
                    reviewAdapter.updateReviews(reviews);
                    updateAverageRating(reviews);
                    reviewRecyclerView.setVisibility(reviews.isEmpty() ? View.GONE : View.VISIBLE);
                } else {
                    showToast("Không thể tải đánh giá");
                    reviewRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<ReviewModel>> call, Throwable t) {
                showToast("Lỗi mạng: " + t.getMessage());
                reviewRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void updateAverageRating(List<ReviewModel> reviews) {
        if (reviews.isEmpty()) {
            bookRatingBar.setRating(0);
            return;
        }
        float averageRating = 0;
        for (ReviewModel review : reviews) {
            averageRating += review.getRating();
        }
        averageRating /= reviews.size();
        bookRatingBar.setRating(averageRating);
    }

    private void showReviewDialog(ReviewModel existingReview) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_review, null);
        RatingBar ratingBar = dialogView.findViewById(R.id.dialogRatingBar);
        EditText commentEditText = dialogView.findViewById(R.id.dialogCommentEditText);
        MaterialButton submitButton = dialogView.findViewById(R.id.dialogSubmitButton);

        if (existingReview != null) {
            ratingBar.setRating(existingReview.getRating());
            commentEditText.setText(existingReview.getComment());
            builder.setTitle("Sửa đánh giá");
        } else {
            builder.setTitle("Thêm đánh giá");
        }

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        submitButton.setOnClickListener(v -> {
            int rating = (int) ratingBar.getRating();
            String comment = commentEditText.getText().toString().trim();

            if (rating < 1) {
                showToast("Vui lòng chọn số sao (1-5)");
                return;
            }
            if (comment.isEmpty()) {
                showToast("Vui lòng nhập bình luận");
                return;
            }

            ReviewRequest request = new ReviewRequest(comment, rating);
            String token = getToken();
            if (existingReview == null) {
                Call<ReviewModel> call = repository.createReview(token, userId, bookId, request);
                call.enqueue(new Callback<ReviewModel>() {
                    @Override
                    public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            showToast("Đánh giá đã được thêm");
                            loadReviews();
                            dialog.dismiss();
                        } else {
                            showToast("Không thể thêm đánh giá. Mã lỗi: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewModel> call, Throwable t) {
                        showToast("Lỗi mạng: " + t.getMessage());
                    }
                });
            } else {
                Call<ReviewModel> call = repository.updateReview(token, existingReview.getId(), request);
                call.enqueue(new Callback<ReviewModel>() {
                    @Override
                    public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            showToast("Đánh giá đã được cập nhật");
                            loadReviews();
                            dialog.dismiss();
                        } else {
                            showToast("Không thể cập nhật đánh giá. Mã lỗi: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewModel> call, Throwable t) {
                        showToast("Lỗi mạng: " + t.getMessage());
                    }
                });
            }
        });

        dialog.show();
    }

    @Override
    public void onEditReview(ReviewModel review) {
        showReviewDialog(review);
    }

    @Override
    public void onDeleteReview(ReviewModel review) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn xóa đánh giá này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    String token = getToken();
                    Call<String> call = repository.deleteReview(token, review.getId());
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                showToast("Đánh giá đã được xóa");
                                loadReviews();
                            } else {
                                showToast("Không thể xóa đánh giá. Mã lỗi: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            showToast("Lỗi mạng: " + t.getMessage());
                        }
                    });
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private String getToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.putExtra("bookId", bookId);
        intent.putExtra("bookTitle", bookTitleArg);
        loginLauncher.launch(intent);
    }

    private void showToast(String message) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToastTime < TOAST_DEBOUNCE_MS) {
            return;
        }
        lastToastTime = currentTime;
        mainHandler.post(() -> {
            if (currentToast != null) {
                currentToast.cancel();
            }
            currentToast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT);
            currentToast.show();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isBuyNowPending", isBuyNowPending);
        outState.putBoolean("isCartCleared", isCartCleared);
        outState.putLong("bookId", bookId);
        outState.putString("bookTitleArg", bookTitleArg);
    }
}