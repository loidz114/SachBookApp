package com.example.sachbook.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import com.example.sachbook.R;
import com.example.sachbook.data.model.ForgotPasswordResponse;
import com.example.sachbook.data.model.RegisterRequest;
import com.example.sachbook.data.model.RegisterResponse;
import com.example.sachbook.data.model.ResetPasswordRequest;
import com.example.sachbook.data.repository.BookRepository;
import com.example.sachbook.ui.viewmodel.RegisterViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class OtpActivity extends AppCompatActivity {
    private RegisterViewModel viewModel;
    private EditText otpInput;
    private Button verifyOtpButton;
    private ProgressBar progressBar;
    private TextView resendOtpLink;
    private ImageButton backButton;
    private RegisterRequest pendingRequest;
    private String email;
    private String source;
    private BookRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
                    return (T) new RegisterViewModel(OtpActivity.this, new SavedStateHandle());
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(RegisterViewModel.class);

        // Khởi tạo repository
        repository = new BookRepository(this);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        pendingRequest = (RegisterRequest) intent.getSerializableExtra("pendingRequest");
        email = intent.getStringExtra("email");
        source = intent.getStringExtra("source");

        // Kiểm tra dữ liệu đầu vào
        if (pendingRequest == null && (email == null || !"forgot_password".equals(source))) {
            Toast.makeText(this, "Error: Invalid data", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(OtpActivity.this, RegisterActivity.class));
            finish();
            return;
        }

        // Ánh xạ view
        otpInput = findViewById(R.id.otpInput);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        progressBar = findViewById(R.id.otpProgressBar);
        resendOtpLink = findViewById(R.id.resendOtpLink);
        backButton = findViewById(R.id.backButton);

        // Xử lý nút Xác minh OTP
        verifyOtpButton.setOnClickListener(v -> {
            String otp = otpInput.getText().toString().trim();
            if (otp.isEmpty()) {
                Toast.makeText(OtpActivity.this, "Vui lòng nhập mã OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            if (otp.length() != 6 || !otp.matches("\\d+")) {
                Toast.makeText(OtpActivity.this, "Mã OTP phải là 6 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("forgot_password".equals(source)) {
                progressBar.setVisibility(View.VISIBLE);
                verifyOtpButton.setEnabled(false);
                resendOtpLink.setEnabled(false);

                ResetPasswordRequest request = new ResetPasswordRequest(email, otp, "");
                Call<ForgotPasswordResponse> call = repository.verifyOtpForgot(request);
                call.enqueue(new Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        verifyOtpButton.setEnabled(true);
                        resendOtpLink.setEnabled(true);

                        System.out.println("Verify OTP response code: " + response.code());
                        if (!response.isSuccessful()) {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                System.out.println("Verify OTP error: " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            ForgotPasswordResponse forgotResponse = response.body();
                            Toast.makeText(OtpActivity.this, forgotResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            if (forgotResponse.isSuccess()) {
                                Intent resetIntent = new Intent(OtpActivity.this, ResetPasswordActivity.class);
                                resetIntent.putExtra("email", email);
                                resetIntent.putExtra("otp", otp);
                                startActivity(resetIntent);
                                finish();
                            }
                        } else {
                            Toast.makeText(OtpActivity.this, "Xác minh OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        verifyOtpButton.setEnabled(true);
                        resendOtpLink.setEnabled(true);
                        Toast.makeText(OtpActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("Network error: " + t.getMessage());
                    }
                });
            } else {
                viewModel.verifyOtp(pendingRequest, otp);
            }
        });

        // Xử lý nút Quay lại
        backButton.setOnClickListener(v -> finish());

        // Xử lý gửi lại OTP
        resendOtpLink.setOnClickListener(v -> {
            if ("forgot_password".equals(source)) {
                progressBar.setVisibility(View.VISIBLE);
                verifyOtpButton.setEnabled(false);
                resendOtpLink.setEnabled(false);

                Call<ForgotPasswordResponse> call = repository.sendResetLink(email);
                call.enqueue(new Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        verifyOtpButton.setEnabled(true);
                        resendOtpLink.setEnabled(true);

                        System.out.println("Resend OTP response code: " + response.code());
                        if (!response.isSuccessful()) {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                System.out.println("Resend OTP error: " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if (response.isSuccessful() && response.body() != null) {
                            ForgotPasswordResponse forgotResponse = response.body();
                            Toast.makeText(OtpActivity.this, forgotResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OtpActivity.this, "Gửi lại OTP thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        verifyOtpButton.setEnabled(true);
                        resendOtpLink.setEnabled(true);
                        Toast.makeText(OtpActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println("Network error: " + t.getMessage());
                    }
                });
            } else {
                viewModel.resendOtp(pendingRequest);
            }
        });

        // Quan sát trạng thái ViewModel (cho luồng đăng ký)
        viewModel.getRegisterResult().observe(this, response -> {
            if (response == null) return;
            Toast.makeText(OtpActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            if (response.isSuccess() && response.getMessage().contains("successfully")) {
                startActivity(new Intent(OtpActivity.this, MainActivity.class));
                finish();
            }
        });

        viewModel.getLoadingState().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            verifyOtpButton.setEnabled(!isLoading);
            resendOtpLink.setEnabled(!isLoading);
        });
    }
}