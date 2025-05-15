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
import com.example.sachbook.data.repository.BookRepository;
import com.example.sachbook.ui.activity.LoginActivity;
import com.example.sachbook.ui.viewmodel.CartViewModel;
import com.example.sachbook.ui.viewmodel.CartViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailFragment extends Fragment {

    private ImageButton backButton;
    private ImageView bookImage;
    private TextView bookTitle, bookAuthor, bookPrice, bookDescription;
    private RatingBar bookRatingBar;
    private RecyclerView reviewRecyclerView;
    private MaterialButton addToCartButton, buyNowButton;
    private BookRepository repository;
    private NavController navController;
    private CartViewModel cartViewModel;
    private long bookId;
    private String bookTitleArg;
    private Toast currentToast;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private long lastToastTime = 0;
    private static final long TOAST_DEBOUNCE_MS = 2000;
    private static final String PREFS_NAME = "SachBookPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private ActivityResultLauncher<Intent> loginLauncher;
    private boolean isBuyNowPending;
    private boolean isClearCartDone; // Track clearCart completion

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

        // Set up NavController
        navController = NavHostFragment.findNavController(this);

        // Restore state
        if (savedInstanceState != null) {
            isBuyNowPending = savedInstanceState.getBoolean("isBuyNowPending", false);
            isClearCartDone = savedInstanceState.getBoolean("isClearCartDone", false);
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

        // Set up RecyclerView for reviews (placeholder)
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewRecyclerView.setVisibility(View.GONE); // Hide until reviews are implemented

        // Set up ActivityResultLauncher for LoginActivity
        loginLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                String token = getToken();
                if (token != null) {
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
                    if (!isClearCartDone) {
                        // After clearCart succeeds, proceed to addToCart
                        isClearCartDone = true;
                        String token = getToken();
                        if (token != null) {
                            cartViewModel.addToCart(token, bookId, 1);
                        }
                    } else {
                        // After addToCart succeeds, fetch total and navigate
                        String token = getToken();
                        if (token != null) {
                            cartViewModel.getCartTotal(token, null);
                        }
                    }
                } else {
                    navController.navigate(R.id.action_book_detail_to_cart);
                }
            } else if (state instanceof CartViewModel.CartState.TotalSuccess && isBuyNowPending) {
                // Navigate to CheckoutFragment after total is updated
                Bundle args = new Bundle();
                args.putBoolean("refreshTotal", true);
                navController.navigate(R.id.action_book_detail_to_checkout, args);
                isBuyNowPending = false;
                isClearCartDone = false; // Reset for next Buy Now
            } else if (state instanceof CartViewModel.CartState.Error) {
                showToast(((CartViewModel.CartState.Error) state).message);
                if (((CartViewModel.CartState.Error) state).message.contains("Phiên đăng nhập")) {
                    navigateToLogin();
                }
                isBuyNowPending = false;
                isClearCartDone = false;
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
            // Show confirmation dialog before clearing cart
            new AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận")
                    .setMessage("Hành động này sẽ xóa giỏ hàng hiện tại. Bạn có muốn tiếp tục?")
                    .setPositiveButton("Có", (dialog, which) -> handleBuyNow(token))
                    .setNegativeButton("Không", null)
                    .show();
        });

        // Load book details
        loadBookDetails();

        return view;
    }

    private void handleBuyNow(String token) {
        isBuyNowPending = true;
        isClearCartDone = false;
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
                    // Use price directly in USD
                    Double priceInUSD = book.getPrice();
                    if (priceInUSD != null && priceInUSD > 0) {
                        bookPrice.setText(String.format("$%.2f", priceInUSD));
                    } else {
                        bookPrice.setText("Price not available");
                        Log.w("BookDetailFragment", "Price is null or 0 for bookId: " + bookId);
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
                    Log.e("BookDetailFragment", "Unsuccessful response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BookModel> call, Throwable t) {
                showToast("Lỗi mạng: " + t.getMessage());
                bookPrice.setText("Price not available");
                Log.e("BookDetailFragment", "Network error: " + t.getMessage());
            }
        });
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
        outState.putBoolean("isClearCartDone", isClearCartDone);
    }
}