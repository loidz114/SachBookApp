package com.example.sachbook.data.remote;

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
import com.example.sachbook.data.model.UpdateProfileRequest;
import com.example.sachbook.data.model.UserModel;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookApiService {
    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("api/auth/verify-otp")
    Call<RegisterResponse> verifyOtp(@Body RegisterRequest request, @Query("otp") String otp);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/forgot-password")
    Call<ForgotPasswordResponse> sendResetLink(@Query("email") String email);

    @POST("api/auth/verify-otp-forgot")
    Call<ForgotPasswordResponse> verifyOtpForgot(@Body ResetPasswordRequest request);
    @POST("api/reviews/user/{userId}/book/{bookId}")
    Call<ReviewModel> createReview(
            @Header("Authorization") String token,
            @Path("userId") Long userId,
            @Path("bookId") Long bookId,
            @Body ReviewRequest request
    );

    @GET("api/reviews/book/{bookId}")
    Call<List<ReviewModel>> getReviewsByBookId(@Path("bookId") Long bookId);

    @PUT("api/reviews/{reviewId}")
    Call<ReviewModel> updateReview(
            @Header("Authorization") String token,
            @Path("reviewId") Long reviewId,
            @Body ReviewRequest request
    );

    @DELETE("api/reviews/{reviewId}")
    Call<String> deleteReview(
            @Header("Authorization") String token,
            @Path("reviewId") Long reviewId
    );
    @POST("api/auth/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest request);

    @GET("api/books/new")
    Call<List<BookModel>> getNewBooks();

    @GET("api/books/{id}")
    Call<BookModel> getBookById(@Path("id") long id);

    @GET("api/books/discounted")
    Call<List<BookModel>> getDiscountedBooks();

    @GET("api/books/search")
    Call<List<BookModel>> searchBooksByCategory(@Query("categoryId") Long categoryId);

    @GET("api/categories")
    Call<List<CategoryModel>> getCategories();

    @GET("api/books/search")
    Call<List<BookModel>> searchBooksByKeyword(@Query("title") String title, @Query("author") String author);

    @GET("api/cart")
    Call<CartModel> getCart(@Header("Authorization") String token);

    @POST("api/cart/add")
    Call<CartModel> addToCart(@Header("Authorization") String token, @Body CartItemRequest request);

    @PUT("api/cart/item/{cartItemId}")
    Call<CartModel> updateCartItem(@Header("Authorization") String token, @Path("cartItemId") long cartItemId, @Query("quantity") int quantity);

    @DELETE("api/cart/item/{cartItemId}")
    Call<Void> removeFromCart(@Header("Authorization") String token, @Path("cartItemId") long cartItemId);

    @DELETE("api/cart/clear")
    Call<Void> clearCart(@Header("Authorization") String token);

    @GET("api/cart/total")
    Call<Double> getCartTotal(@Header("Authorization") String token, @Query("discountCode") String discountCode);

    @POST("api/orders")
    Call<OrderModel> createOrder(@Header("Authorization") String token, @Body OrderRequest request);

    @GET("api/orders")
    Call<List<OrderModel>> getUserOrders(@Header("Authorization") String token);

    @GET("api/orders/{orderId}")
    Call<OrderModel> getUserOrderById(@Header("Authorization") String token, @Path("orderId") long orderId);

    @PUT("api/orders/cancel/{orderId}")
    Call<OrderModel> cancelOrder(@Header("Authorization") String token, @Path("orderId") long orderId);

    @POST("api/payments")
    Call<PaymentResponse> processPayment(@Header("Authorization") String token, @Body PaymentRequest request);

    @GET("/api/auth/profile")
    Call<UserModel> getUserProfile(@Header("Authorization") String token);

    @PUT("/api/auth/profile")
    Call<Void> updateProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);

    @POST("/api/auth/logout")
    Call<Void> logout(@Header("Authorization") String token);
}