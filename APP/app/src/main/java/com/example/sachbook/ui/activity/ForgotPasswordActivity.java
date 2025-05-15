package com.example.sachbook.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.sachbook.R;
import com.example.sachbook.data.model.ForgotPasswordResponse;
import com.example.sachbook.data.repository.BookRepository;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.IOException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private Button sendResetLinkButton;
    private ProgressBar forgotProgressBar;
    private ImageButton backButton;
    private BookRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo repository
        repository = new BookRepository(this);

        // Ánh xạ các view
        emailInput = findViewById(R.id.emailInput);
        sendResetLinkButton = findViewById(R.id.sendResetLinkButton);
        forgotProgressBar = findViewById(R.id.forgotProgressBar);
        backButton = findViewById(R.id.backButton);

        // Xử lý nút Quay lại
        backButton.setOnClickListener(v -> finish());

        // Xử lý nút Gửi OTP
        sendResetLinkButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }

            forgotProgressBar.setVisibility(View.VISIBLE);
            sendResetLinkButton.setEnabled(false);

            Call<ForgotPasswordResponse> call = repository.sendResetLink(email);
            call.enqueue(new Callback<ForgotPasswordResponse>() {
                @Override
                public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                    forgotProgressBar.setVisibility(View.GONE);
                    sendResetLinkButton.setEnabled(true);

                    System.out.println("Send OTP response code: " + response.code());
                    if (!response.isSuccessful()) {
                        try {
                            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                            System.out.println("Send OTP error: " + errorBody);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        ForgotPasswordResponse forgotResponse = response.body();
                        Toast.makeText(ForgotPasswordActivity.this, forgotResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        if (forgotResponse.isSuccess()) {
                            Intent intent = new Intent(ForgotPasswordActivity.this, OtpActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("source", "forgot_password");
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                    forgotProgressBar.setVisibility(View.GONE);
                    sendResetLinkButton.setEnabled(true);
                    Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    System.out.println("Network error: " + t.getMessage());
                }
            });
        });
    }
}