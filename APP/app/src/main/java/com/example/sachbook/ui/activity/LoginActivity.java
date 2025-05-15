package com.example.sachbook.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.sachbook.R;
import com.example.sachbook.ui.viewmodel.LoginViewModel;
import com.example.sachbook.ui.viewmodel.LoginViewModel.LoginState;
import com.example.sachbook.ui.viewmodel.LoginViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText usernameInput, passwordInput;
    private Button loginButton;
    private ProgressBar loginProgressBar;
    private TextView forgotPasswordText, registerLink;
    private LoginViewModel viewModel;
    private Toast currentToast;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private long lastToastTime = 0;
    private static final long TOAST_DEBOUNCE_MS = 2000;
    private static final String PREFS_NAME = "SachBookPrefs";
    private static final String KEY_TOKEN = "auth_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize ViewModel
        LoginViewModelFactory factory = new LoginViewModelFactory(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        // Check login status
        if (viewModel.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        // Bind views
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        loginProgressBar = findViewById(R.id.loginProgressBar);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        registerLink = findViewById(R.id.registerLink);

        // Observe login state
        viewModel.getLoginState().observe(this, state -> {
            loginProgressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);

            if (state instanceof LoginState.Loading) {
                loginProgressBar.setVisibility(View.VISIBLE);
                loginButton.setEnabled(false);
            } else if (state instanceof LoginState.Success) {
                // Save token
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putString(KEY_TOKEN, ((LoginState.Success) state).getToken()).apply();
                showToast(((LoginState.Success) state).message);
                setResult(RESULT_OK); // Set result for caller
                navigateToMainActivity();
            } else if (state instanceof LoginState.Error) {
                showToast(((LoginState.Error) state).message);
                if (((LoginState.Error) state).message.contains("tên đăng nhập")) {
                    usernameInput.setError(((LoginState.Error) state).message);
                    usernameInput.requestFocus();
                } else if (((LoginState.Error) state).message.contains("mật khẩu")) {
                    passwordInput.setError(((LoginState.Error) state).message);
                    passwordInput.requestFocus();
                }
            }
        });

        // Handle login button
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            viewModel.login(username, password);
        });

        // Handle forgot password
        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Handle register
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToastTime < TOAST_DEBOUNCE_MS) {
            return;
        }
        lastToastTime = currentTime;
        mainHandler.post(() -> {
            if (currentToast != null) {
                currentToast.cancel();
            }
            currentToast = Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT);
            currentToast.show();
        });
    }
}