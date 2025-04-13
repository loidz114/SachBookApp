package com.example.sachbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái đăng nhập
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            // Nếu đã đăng nhập, chuyển thẳng đến MainActivity (trang chủ)
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // Ánh xạ các thành phần giao diện
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // Xử lý sự kiện khi nhấn nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra thông tin đăng nhập với SharedPreferences
            String savedUsername = prefs.getString("username", "");
            String savedPassword = prefs.getString("password", "");

            if (username.equals(savedUsername) && password.equals(savedPassword)) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                // Lưu trạng thái đăng nhập
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();

                // Chuyển đến MainActivity (trang chủ)
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Tên người dùng hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện khi nhấn vào "Chưa có tài khoản? Đăng ký"
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        // Xử lý sự kiện khi nhấn vào "Quên mật khẩu?"
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
}