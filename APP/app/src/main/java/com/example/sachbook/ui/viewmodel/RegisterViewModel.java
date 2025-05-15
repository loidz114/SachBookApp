package com.example.sachbook.ui.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.example.sachbook.data.model.RegisterRequest;
import com.example.sachbook.data.model.RegisterResponse;
import com.example.sachbook.data.repository.BookRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {
    private final BookRepository repository;
    private final MutableLiveData<RegisterResponse> registerResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final SavedStateHandle savedStateHandle;
    private static final String PENDING_REQUEST_KEY = "pending_request";

    public RegisterViewModel(Context context, SavedStateHandle savedStateHandle) {
        this.repository = new BookRepository(context);
        this.savedStateHandle = savedStateHandle;
    }

    public void register(String username, String name, String phone, String email, String address, String password, String confirmPassword) {
        // Kiểm tra xác nhận mật khẩu
        if (!password.equals(confirmPassword)) {
            registerResult.setValue(new RegisterResponse(false, "Passwords do not match"));
            return;
        }

        // Kiểm tra dữ liệu đầu vào
        if (username.isEmpty() || name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty()) {
            registerResult.setValue(new RegisterResponse(false, "Please fill in all fields"));
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            registerResult.setValue(new RegisterResponse(false, "Invalid email format"));
            return;
        }
        if (phone.length() < 10 || phone.length() > 15) {
            registerResult.setValue(new RegisterResponse(false, "Phone must be between 10 and 15 characters"));
            return;
        }
        if (password.length() < 6) {
            registerResult.setValue(new RegisterResponse(false, "Password must be at least 6 characters"));
            return;
        }

        isLoading.setValue(true);

        RegisterRequest request = new RegisterRequest(username, name, phone, email, address, password);
        savedStateHandle.set(PENDING_REQUEST_KEY, request); // Lưu vào SavedStateHandle
        repository.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    registerResult.setValue(response.body());
                } else {
                    String errorMessage = "Server error";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    registerResult.setValue(new RegisterResponse(false, errorMessage));
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                isLoading.setValue(false);
                registerResult.setValue(new RegisterResponse(false, t.getMessage()));
            }
        });
    }

    public void verifyOtp(RegisterRequest request, String otp) {
        isLoading.setValue(true);
        repository.verifyOtp(request, otp).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    registerResult.setValue(response.body());
                } else {
                    String errorMessage = "Server error";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    registerResult.setValue(new RegisterResponse(false, errorMessage));
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                isLoading.setValue(false);
                registerResult.setValue(new RegisterResponse(false, t.getMessage()));
            }
        });
    }

    public void resendOtp(RegisterRequest request) {
        isLoading.setValue(true);
        repository.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    registerResult.setValue(response.body());
                } else {
                    String errorMessage = "Server error";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    registerResult.setValue(new RegisterResponse(false, errorMessage));
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                isLoading.setValue(false);
                registerResult.setValue(new RegisterResponse(false, t.getMessage()));
            }
        });
    }

    public LiveData<RegisterResponse> getRegisterResult() {
        return registerResult;
    }

    public LiveData<Boolean> getLoadingState() {
        return isLoading;
    }

    public RegisterRequest getPendingRequest() {
        return savedStateHandle.get(PENDING_REQUEST_KEY);
    }
}