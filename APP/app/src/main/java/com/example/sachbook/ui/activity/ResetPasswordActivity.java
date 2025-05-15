package com.example.sachbook.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sachbook.R;
import com.example.sachbook.data.model.ResetPasswordRequest;
import com.example.sachbook.data.model.ResetPasswordResponse;
import com.example.sachbook.data.repository.BookRepository;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class ResetPasswordActivity extends AppCompatActivity {
    private TextInputEditText newPasswordInput, confirmPasswordInput;
    private Button resetButton;
    private ProgressBar resetProgressBar;
    private ImageButton backButton;
    private String email;
    private String otp;
    private BookRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Khởi tạo repository
        repository = new BookRepository(this);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        otp = intent.getStringExtra("otp");

        if (email == null || otp == null) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        System.out.println("Email received: " + email + ", OTP: " + otp);

        // Ánh xạ các view
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        resetButton = findViewById(R.id.resetButton);
        resetProgressBar = findViewById(R.id.resetProgressBar);
        backButton = findViewById(R.id.backButton);

        // Xử lý nút Back
        backButton.setOnClickListener(v -> finish());

        // Xử lý nút Đặt Lại Mật Khẩu
        resetButton.setOnClickListener(v -> {
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // Kiểm tra đầu vào
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ResetPasswordActivity.this, "Vui lòng điền đầy đủ các trường", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ResetPasswordActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPassword.length() < 6) {
                Toast.makeText(ResetPasswordActivity.this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi yêu cầu đặt lại mật khẩu
            resetProgressBar.setVisibility(View.VISIBLE);
            resetButton.setEnabled(false);

            System.out.println("Sending reset password request: email=" + email + ", otp=" + otp + ", newPassword=" + newPassword);
            ResetPasswordRequest request = new ResetPasswordRequest(email, otp, newPassword);
            Call<ResetPasswordResponse> call = repository.resetPassword(request);
            call.enqueue(new Callback<ResetPasswordResponse>() {
                @Override
                public void onResponse(Call<ResetPasswordResponse> call, Response<ResetPasswordResponse> response) {
                    resetProgressBar.setVisibility(View.GONE);
                    resetButton.setEnabled(true);

                    System.out.println("Reset password response code: " + response.code());
                    if (!response.isSuccessful()) {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            System.out.println("Reset password error: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        ResetPasswordResponse resetResponse = response.body();
                        Toast.makeText(ResetPasswordActivity.this, resetResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (resetResponse.isSuccess()) {
                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Đặt lại mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResetPasswordResponse> call, Throwable t) {
                    resetProgressBar.setVisibility(View.GONE);
                    resetButton.setEnabled(true);
                    Toast.makeText(ResetPasswordActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Network error: " + t.getMessage());
                }
            });
        });
    }
}