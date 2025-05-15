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
import com.example.sachbook.data.model.RegisterRequest;
import com.example.sachbook.data.model.RegisterResponse;
import com.example.sachbook.ui.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {
    private RegisterViewModel viewModel;
    private EditText usernameInput, nameInput, phoneInput, emailInput, addressInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private ProgressBar progressBar;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo ViewModel với SavedStateHandle
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
                if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
                    return (T) new RegisterViewModel(RegisterActivity.this, new SavedStateHandle());
                }
                throw new IllegalArgumentException("Unknown ViewModel class");
            }
        }).get(RegisterViewModel.class);

        // Ánh xạ các view
        usernameInput = findViewById(R.id.usernameInput);
        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        emailInput = findViewById(R.id.emailInput);
        addressInput = findViewById(R.id.addressInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.registerProgressBar);
        backButton = findViewById(R.id.backButton);

        // Xử lý sự kiện nhấn nút đăng ký
        registerButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String name = nameInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String address = addressInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            viewModel.register(username, name, phone, email, address, password, confirmPassword);
        });

        // Xử lý nút Back để chuyển về LoginActivity
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // Quan sát trạng thái từ ViewModel
        viewModel.getRegisterResult().observe(this, response -> {
            Toast.makeText(RegisterActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            if (response.isSuccess() && response.getMessage().contains("OTP sent")) {
                RegisterRequest pendingRequest = viewModel.getPendingRequest();
                if (pendingRequest != null) {
                    Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                    intent.putExtra("pendingRequest", pendingRequest);
                    startActivity(intent);
                    finish(); // Kết thúc RegisterActivity
                } else {
                    Toast.makeText(this, "Error: No pending request", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getLoadingState().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            registerButton.setEnabled(!isLoading);
        });
    }
}