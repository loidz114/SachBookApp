package com.example.sachbook.ui.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.sachbook.data.model.LoginRequest;
import com.example.sachbook.data.model.LoginResponse;
import com.example.sachbook.data.repository.BookRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private final BookRepository repository;
    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>();

    public LoginViewModel(Application application) {
        super(application);
        repository = new BookRepository(application);
    }

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    public boolean isLoggedIn() {
        // Check if token exists in SharedPreferences
        SharedPreferences prefs = getApplication().getSharedPreferences("SachBookPrefs", Context.MODE_PRIVATE);
        return prefs.getString("auth_token", null) != null;
    }

    public void login(String username, String password) {
        loginState.setValue(new LoginState.Loading());
        LoginRequest request = new LoginRequest(username, password);
        repository.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String token = response.body().getToken();
                    loginState.setValue(new LoginState.Success("Đăng nhập thành công", token));
                } else {
                    loginState.setValue(new LoginState.Error("Tên đăng nhập hoặc mật khẩu không đúng"));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginState.setValue(new LoginState.Error("Lỗi mạng: " + t.getMessage()));
            }
        });
    }

    public static class LoginState {
        public static class Loading extends LoginState {}
        public static class Success extends LoginState {
            public final String message;
            public final String token;
            public Success(String message, String token) {
                this.message = message;
                this.token = token;
            }
            public String getToken() {
                return token;
            }
        }
        public static class Error extends LoginState {
            public final String message;
            public Error(String message) {
                this.message = message;
            }
        }
    }
}