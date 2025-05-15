package com.example.sachbook.ui.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sachbook.data.model.CartItemRequest;
import com.example.sachbook.data.model.CartModel;
import com.example.sachbook.data.model.OrderModel;
import com.example.sachbook.data.model.OrderRequest;
import com.example.sachbook.data.repository.BookRepository;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for managing cart, order, and payment operations.
 */
public class CartViewModel extends AndroidViewModel {
    private final BookRepository repository;
    private final MutableLiveData<CartState> cartState = new MutableLiveData<>();
    private final Map<String, String> errorMessageMap;
    private static final int MAX_RETRIES = 3; // Số lần thử lại tối đa
    private static final String TAG = "CartViewModel";

    public CartViewModel(@NonNull Application application) {
        super(application);
        repository = new BookRepository(application);
        // Khởi tạo ánh xạ thông điệp lỗi thân thiện với người dùng
        errorMessageMap = new HashMap<>();
        errorMessageMap.put("Cart is empty", "Giỏ hàng trống, vui lòng thêm sản phẩm");
        errorMessageMap.put("Invalid payment method", "Phương thức thanh toán không hợp lệ, vui lòng chọn lại");
        errorMessageMap.put("Shipping address cannot be empty", "Vui lòng nhập địa chỉ giao hàng");
        errorMessageMap.put("Shipping address is required and cannot be empty", "Vui lòng nhập địa chỉ giao hàng");
        errorMessageMap.put("Invalid discount code", "Mã giảm giá không hợp lệ hoặc đã hết hạn");
        errorMessageMap.put("Insufficient stock for book", "Sản phẩm không đủ tồn kho, vui lòng kiểm tra lại");
        errorMessageMap.put("Phiên đăng nhập hết hạn", "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại");
        errorMessageMap.put("Field 'shipping_address' doesn't have a default value", "Địa chỉ giao hàng không hợp lệ, vui lòng kiểm tra lại");
    }

    public LiveData<CartState> getCartState() {
        return cartState;
    }

    // Lấy giỏ hàng từ backend
    public void getCart(@NonNull String token) {
        cartState.setValue(new CartState.Loading());
        repository.getCart(token).enqueue(new Callback<CartModel>() {
            @Override
            public void onResponse(@NonNull Call<CartModel> call, @NonNull Response<CartModel> response) {
                Log.d(TAG, "GetCart Response: " + response);
                if (response.isSuccessful() && response.body() != null) {
                    CartModel cart = response.body();
                    if (cart.getItems() == null) {
                        cart.setItems(new ArrayList<>());
                    }
                    cartState.setValue(new CartState.Success(cart, "Tải giỏ hàng thành công"));
                } else {
                    handleErrorResponse(response, "Tải giỏ hàng thất bại");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartModel> call, @NonNull Throwable t) {
                handleNetworkError(t, "Network: Lỗi tải giỏ hàng");
            }
        });
    }

    // Thêm sản phẩm vào giỏ hàng
    public void addToCart(@NonNull String token, long bookId, int quantity) {
        if (quantity <= 0) {
            cartState.setValue(new CartState.Error("Số lượng phải lớn hơn 0"));
            return;
        }
        cartState.setValue(new CartState.Loading());
        CartItemRequest request = new CartItemRequest(bookId, quantity);
        repository.addToCart(token, request).enqueue(new Callback<CartModel>() {
            @Override
            public void onResponse(@NonNull Call<CartModel> call, @NonNull Response<CartModel> response) {
                Log.d(TAG, "AddToCart Response: " + response);
                if (response.isSuccessful() && response.body() != null) {
                    CartModel cart = response.body();
                    if (cart.getItems() == null) {
                        cart.setItems(new ArrayList<>());
                    }
                    cartState.setValue(new CartState.Success(cart, "Thêm vào giỏ hàng thành công"));
                } else {
                    handleErrorResponse(response, "Thêm vào giỏ hàng thất bại");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartModel> call, @NonNull Throwable t) {
                handleNetworkError(t, "Network: Lỗi thêm vào giỏ hàng");
            }
        });
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    public void updateCartItem(@NonNull String token, long cartItemId, int quantity) {
        if (quantity < 0) {
            cartState.setValue(new CartState.Error("Số lượng không thể âm"));
            return;
        }
        cartState.setValue(new CartState.Loading());
        repository.updateCartItem(token, cartItemId, quantity).enqueue(new Callback<CartModel>() {
            @Override
            public void onResponse(@NonNull Call<CartModel> call, @NonNull Response<CartModel> response) {
                Log.d(TAG, "UpdateCartItem Response: " + response);
                if (response.isSuccessful() && response.body() != null) {
                    CartModel cart = response.body();
                    if (cart.getItems() == null) {
                        cart.setItems(new ArrayList<>());
                    }
                    cartState.setValue(new CartState.Success(cart, "Cập nhật giỏ hàng thành công"));
                } else {
                    handleErrorResponse(response, "Cập nhật giỏ hàng thất bại");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CartModel> call, @NonNull Throwable t) {
                handleNetworkError(t, "Network: Lỗi cập nhật giỏ hàng");
            }
        });
    }

    // Xóa sản phẩm khỏi giỏ hàng
    public void removeFromCart(@NonNull String token, long cartItemId) {
        cartState.setValue(new CartState.Loading());
        repository.removeFromCart(token, cartItemId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d(TAG, "RemoveFromCart Response: " + response);
                if (response.isSuccessful()) {
                    getCart(token);
                } else {
                    handleErrorResponse(response, "Xóa mục khỏi giỏ hàng thất bại");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                handleNetworkError(t, "Network: Lỗi xóa mục khỏi giỏ hàng");
            }
        });
    }

    // Xóa toàn bộ giỏ hàng
    public void clearCart(@NonNull String token) {
        cartState.setValue(new CartState.Loading());
        repository.clearCart(token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d(TAG, "ClearCart Response: " + response);
                if (response.isSuccessful()) {
                    getCart(token);
                } else {
                    handleErrorResponse(response, "Xóa giỏ hàng thất bại");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                handleNetworkError(t, "Network: Lỗi xóa giỏ hàng");
            }
        });
    }

    // Tính tổng giá trị giỏ hàng
    public void getCartTotal(@NonNull String token, String discountCode) {
        cartState.setValue(new CartState.Loading());
        repository.getCartTotal(token, discountCode).enqueue(new Callback<Double>() {
            @Override
            public void onResponse(@NonNull Call<Double> call, @NonNull Response<Double> response) {
                Log.d(TAG, "GetCartTotal Response: " + response);
                if (response.isSuccessful() && response.body() != null) {
                    cartState.setValue(new CartState.TotalSuccess(response.body()));
                } else {
                    handleErrorResponse(response, "Tính tổng giỏ hàng thất bại");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Double> call, @NonNull Throwable t) {
                handleNetworkError(t, "Network: Lỗi tính tổng giỏ hàng");
            }
        });
    }

    // Tạo đơn hàng
    public void createOrder(@NonNull String token, @NonNull OrderRequest request) {
        if (request.getPaymentMethod() == null || request.getShippingAddress() == null) {
            cartState.setValue(new CartState.Error("Phương thức thanh toán và địa chỉ giao hàng không được để trống"));
            return;
        }
        cartState.setValue(new CartState.Loading());
        createOrderWithRetry(token, request, MAX_RETRIES, 0);
    }

    // Tạo đơn hàng với cơ chế thử lại
    private void createOrderWithRetry(@NonNull String token, @NonNull OrderRequest request, int maxRetries, int retryCount) {
        repository.createOrder(token, request).enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(@NonNull Call<OrderModel> call, @NonNull Response<OrderModel> response) {
                Log.d(TAG, "CreateOrder Response: " + response.raw());
                Log.d(TAG, "CreateOrder Response Body: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    OrderModel order = response.body();
                    getCart(token); // Làm mới giỏ hàng sau khi tạo đơn thành công
                    cartState.setValue(new CartState.OrderSuccess(order));
                } else {
                    handleErrorResponse(response, "Tạo đơn hàng thất bại");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderModel> call, @NonNull Throwable t) {
                Log.e(TAG, "CreateOrder Failure: " + t.getMessage(), t);
                if (retryCount < maxRetries) {
                    Log.d(TAG, "Retrying createOrder, attempt: " + (retryCount + 1));
                    createOrderWithRetry(token, request, maxRetries, retryCount + 1);
                } else {
                    handleNetworkError(t, "Network: Lỗi tạo đơn hàng");
                }
            }
        });
    }

    // Lấy danh sách đơn hàng của người dùng
    public void getUserOrders(@NonNull String token) {
        cartState.setValue(new CartState.Loading());
        getUserOrdersWithRetry(token, MAX_RETRIES, 0);
    }

    // Lấy danh sách đơn hàng với cơ chế thử lại
    private void getUserOrdersWithRetry(@NonNull String token, int maxRetries, int retryCount) {
        repository.getUserOrders(token).enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderModel>> call, @NonNull Response<List<OrderModel>> response) {
                Log.d(TAG, "GetUserOrders Response: " + response);
                if (response.isSuccessful() && response.body() != null) {
                    cartState.setValue(new CartState.OrderHistorySuccess(response.body()));
                } else {
                    handleErrorResponse(response, "Tải lịch sử đơn hàng thất bại");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderModel>> call, @NonNull Throwable t) {
                Log.e(TAG, "GetUserOrders Failure: " + t.getMessage(), t);
                if (retryCount < maxRetries) {
                    Log.d(TAG, "Retrying getUserOrders, attempt: " + (retryCount + 1));
                    getUserOrdersWithRetry(token, maxRetries, retryCount + 1);
                } else {
                    handleNetworkError(t, "Network: Lỗi tải lịch sử đơn hàng");
                }
            }
        });
    }

    // Lấy chi tiết đơn hàng theo ID
    public void getUserOrderById(@NonNull String token, long orderId) {
        cartState.setValue(new CartState.Loading());
        repository.getUserOrderById(token, orderId).enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(@NonNull Call<OrderModel> call, @NonNull Response<OrderModel> response) {
                Log.d(TAG, "GetUserOrderById Response: " + response);
                if (response.isSuccessful() && response.body() != null) {
                    cartState.setValue(new CartState.OrderSuccess(response.body()));
                } else {
                    handleErrorResponse(response, "Tải chi tiết đơn hàng thất bại");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderModel> call, @NonNull Throwable t) {
                handleNetworkError(t, "Network: Lỗi tải chi tiết đơn hàng");
            }
        });
    }

    // Hủy đơn hàng
    public void cancelOrder(@NonNull String token, long orderId) {
        cartState.setValue(new CartState.Loading());
        cancelOrderWithRetry(token, orderId, MAX_RETRIES, 0);
    }

    // Hủy đơn hàng với cơ chế thử lại
    private void cancelOrderWithRetry(@NonNull String token, long orderId, int maxRetries, int retryCount) {
        repository.cancelOrder(token, orderId).enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(@NonNull Call<OrderModel> call, @NonNull Response<OrderModel> response) {
                Log.d(TAG, "CancelOrder Response: " + response);
                if (response.isSuccessful() && response.body() != null) {
                    cartState.setValue(new CartState.OrderSuccess(response.body()));
                } else {
                    handleErrorResponse(response, "Hủy đơn hàng thất bại");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderModel> call, @NonNull Throwable t) {
                Log.e(TAG, "CancelOrder Failure: " + t.getMessage(), t);
                if (retryCount < maxRetries) {
                    Log.d(TAG, "Retrying cancelOrder, attempt: " + (retryCount + 1));
                    cancelOrderWithRetry(token, orderId, maxRetries, retryCount + 1);
                } else {
                    handleNetworkError(t, "Network: Lỗi hủy đơn hàng");
                }
            }
        });
    }

    // Xử lý lỗi phản hồi từ backend
    private void handleErrorResponse(@NonNull Response<?> response, @NonNull String defaultMessage) {
        String errorMessage = defaultMessage;
        if (response.errorBody() != null) {
            try {
                errorMessage = response.errorBody().string();
                // Ánh xạ lỗi backend thành thông điệp thân thiện
                for (Map.Entry<String, String> entry : errorMessageMap.entrySet()) {
                    if (errorMessage.contains(entry.getKey())) {
                        errorMessage = entry.getValue();
                        break;
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error parsing error body: " + e.getMessage());
            }
        }
        if (response.code() == 401) {
            cartState.setValue(new CartState.Error("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại"));
        } else if (response.code() == 400) {
            cartState.setValue(new CartState.Error(errorMessage));
        } else if (response.code() == 404) {
            cartState.setValue(new CartState.Error("Không tìm thấy tài nguyên"));
        } else {
            cartState.setValue(new CartState.Error(defaultMessage + ": " + response.message()));
        }
    }

    // Xử lý lỗi mạng với thông điệp cụ thể
    private void handleNetworkError(Throwable t, String defaultMessage) {
        String errorMessage;
        if (t instanceof SocketTimeoutException) {
            errorMessage = "Network: Máy chủ phản hồi chậm, vui lòng thử lại";
        } else if (t instanceof UnknownHostException) {
            errorMessage = "Network: Không tìm thấy máy chủ, kiểm tra kết nối mạng";
        } else if (t instanceof IOException) {
            errorMessage = "Network: Lỗi kết nối mạng, vui lòng kiểm tra kết nối";
        } else {
            errorMessage = defaultMessage + ": " + t.getMessage();
        }
        cartState.setValue(new CartState.Error(errorMessage));
    }

    // Định nghĩa các trạng thái của giỏ hàng và đơn hàng
    public static class CartState {
        public static class Loading extends CartState {}
        public static class Success extends CartState {
            public final CartModel cart;
            public final String message;
            public Success(CartModel cart, @NonNull String message) {
                this.cart = cart;
                this.message = message;
            }
        }
        public static class TotalSuccess extends CartState {
            public final double total;
            public TotalSuccess(double total) {
                this.total = total;
            }
        }
        public static class OrderSuccess extends CartState {
            public final OrderModel order;
            public OrderSuccess(@NonNull OrderModel order) {
                this.order = order;
            }
        }
        public static class OrderHistorySuccess extends CartState {
            public final List<OrderModel> orders;
            public OrderHistorySuccess(@NonNull List<OrderModel> orders) {
                this.orders = orders;
            }
        }
        public static class Error extends CartState {
            public final String message;
            public Error(@NonNull String message) {
                this.message = message;
            }
        }
    }
}