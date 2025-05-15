package com.example.sachbook.ui.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sachbook.data.model.ForgotPasswordResponse;
import com.example.sachbook.data.repository.BookRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordViewModel extends ViewModel {
    private final BookRepository repository;
    private final MutableLiveData<ForgotPasswordResponse> forgotResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ForgotPasswordViewModel(Context context) {
        this.repository = new BookRepository(context);
    }

    public void sendResetLink(String email) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.setValue("Invalid email format");
            return;
        }

        isLoading.setValue(true);
        Call<ForgotPasswordResponse> call = repository.sendResetLink(email);
        call.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    forgotResult.setValue(response.body());
                } else {
                    errorMessage.setValue("Failed to send OTP");
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }

    public LiveData<ForgotPasswordResponse> getForgotResult() {
        return forgotResult;
    }

    public LiveData<Boolean> getLoadingState() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}