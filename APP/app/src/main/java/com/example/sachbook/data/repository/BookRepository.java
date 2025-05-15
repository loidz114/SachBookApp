package com.example.sachbook.data.repository;

import android.content.Context;
import com.example.sachbook.data.model.BookModel;
import com.example.sachbook.data.model.CartItemRequest;
import com.example.sachbook.data.model.CartModel;
import com.example.sachbook.data.model.CategoryModel;
import com.example.sachbook.data.model.ForgotPasswordResponse;
import com.example.sachbook.data.model.LoginRequest;
import com.example.sachbook.data.model.LoginResponse;
import com.example.sachbook.data.model.OrderModel;
import com.example.sachbook.data.model.OrderRequest;
import com.example.sachbook.data.model.PaymentRequest;
import com.example.sachbook.data.model.PaymentResponse;
import com.example.sachbook.data.model.RegisterRequest;
import com.example.sachbook.data.model.RegisterResponse;
import com.example.sachbook.data.model.ResetPasswordRequest;
import com.example.sachbook.data.model.ResetPasswordResponse;
import com.example.sachbook.data.model.ReviewModel;
import com.example.sachbook.data.model.ReviewRequest;
import com.example.sachbook.data.model.UserModel;
import com.example.sachbook.data.remote.ApiClient;
import com.example.sachbook.data.remote.BookApiService;
import java.util.List;
import retrofit2.Call;

public class BookRepository {
    private final BookApiService apiService;

    public BookRepository(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        this.apiService = ApiClient.getBookApiService(context);
    }

    public Call<RegisterResponse> register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("RegisterRequest cannot be null");
        }
        return apiService.register(request);
    }
    public Call<ReviewModel> createReview(String token, Long userId, Long bookId, ReviewRequest request) {
        if (token == null || userId == null || bookId == null || request == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        return apiService.createReview("Bearer " + token, userId, bookId, request);
    }

    public Call<List<ReviewModel>> getReviewsByBookId(Long bookId) {
        if (bookId == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }
        return apiService.getReviewsByBookId(bookId);
    }

    public Call<ReviewModel> updateReview(String token, Long reviewId, ReviewRequest request) {
        if (token == null || reviewId == null || request == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        return apiService.updateReview("Bearer " + token, reviewId, request);
    }
    public Call<UserModel> getUserProfile(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return apiService.getUserProfile("Bearer " + token);
    }
    public Call<String> deleteReview(String token, Long reviewId) {
        if (token == null || reviewId == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        return apiService.deleteReview("Bearer " + token, reviewId);
    }
    public Call<RegisterResponse> verifyOtp(RegisterRequest request, String otp) {
        if (request == null || otp == null) {
            throw new IllegalArgumentException("RegisterRequest and OTP cannot be null");
        }
        return apiService.verifyOtp(request, otp);
    }

    public Call<LoginResponse> login(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("LoginRequest cannot be null");
        }
        return apiService.login(request);
    }

    public Call<ForgotPasswordResponse> sendResetLink(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return apiService.sendResetLink(email);
    }

    public Call<ForgotPasswordResponse> verifyOtpForgot(ResetPasswordRequest request) {
        if (request == null || request.getEmail() == null || request.getOtp() == null) {
            throw new IllegalArgumentException("ResetPasswordRequest, email, and OTP cannot be null");
        }
        return apiService.verifyOtpForgot(request);
    }

    public Call<ResetPasswordResponse> resetPassword(ResetPasswordRequest request) {
        if (request == null || request.getEmail() == null || request.getOtp() == null || request.getNewPassword() == null) {
            throw new IllegalArgumentException("ResetPasswordRequest, email, OTP, and newPassword cannot be null");
        }
        return apiService.resetPassword(request);
    }

    public Call<List<BookModel>> getNewBooks() {
        return apiService.getNewBooks();
    }

    public Call<List<BookModel>> getDiscountedBooks() {
        return apiService.getDiscountedBooks();
    }

    public Call<List<BookModel>> searchBooksByCategory(Long categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("Category ID must be a positive number");
        }
        return apiService.searchBooksByCategory(categoryId);
    }

    public Call<List<CategoryModel>> getCategories() {
        return apiService.getCategories();
    }

    public Call<BookModel> getBookById(long bookId) {
        return apiService.getBookById(bookId);
    }

    public Call<List<BookModel>> searchBooksByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Keyword cannot be null or empty");
        }
        return apiService.searchBooksByKeyword(keyword,keyword);
    }

    public Call<CartModel> getCart(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return apiService.getCart("Bearer " + token);
    }

    public Call<CartModel> addToCart(String token, CartItemRequest request) {
        if (token == null || request == null || request.getBookId() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Token, CartItemRequest, bookId, and quantity must be valid");
        }
        return apiService.addToCart("Bearer " + token, request);
    }

    public Call<CartModel> updateCartItem(String token, long cartItemId, int quantity) {
        if (token == null || quantity < 0) {
            throw new IllegalArgumentException("Token must be valid and quantity must be non-negative");
        }
        return apiService.updateCartItem("Bearer " + token, cartItemId, quantity);
    }

    public Call<Void> removeFromCart(String token, long cartItemId) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return apiService.removeFromCart("Bearer " + token, cartItemId);
    }

    public Call<Void> clearCart(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return apiService.clearCart("Bearer " + token);
    }

    public Call<Double> getCartTotal(String token, String discountCode) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return apiService.getCartTotal("Bearer " + token, discountCode);
    }

    public Call<OrderModel> createOrder(String token, OrderRequest request) {
        if (token == null || request == null || request.getPaymentMethod() == null || request.getShippingAddress() == null) {
            throw new IllegalArgumentException("Token, OrderRequest, paymentMethod, and shippingAddress must be valid");
        }
        return apiService.createOrder("Bearer " + token, request);
    }

    public Call<List<OrderModel>> getUserOrders(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return apiService.getUserOrders("Bearer " + token);
    }

    public Call<OrderModel> getUserOrderById(String token, long orderId) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return apiService.getUserOrderById("Bearer " + token, orderId);
    }

    public Call<OrderModel> cancelOrder(String token, long orderId) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }
        return apiService.cancelOrder("Bearer " + token, orderId);
    }

    public Call<PaymentResponse> processPayment(String token, PaymentRequest request) {
        if (token == null || request == null || request.getOrderId() == null || request.getPaymentMethod() == null || request.getTransactionId() == null) {
            throw new IllegalArgumentException("Token, PaymentRequest, orderId, paymentMethod, and transactionId must be valid");
        }
        return apiService.processPayment("Bearer " + token, request);
    }
}