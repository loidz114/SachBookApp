package com.example.sachbook;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private Button btnSendResetEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // XML mới đã sửa dùng email

        // Ánh xạ view
        etEmail = findViewById(R.id.etEmail);
        btnSendResetEmail = findViewById(R.id.btnSendResetEmail);

        mAuth = FirebaseAuth.getInstance();

        btnSendResetEmail.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đã gửi email đặt lại mật khẩu. Kiểm tra hộp thư đến của bạn.", Toast.LENGTH_LONG).show();
                            finish(); // Quay lại màn hình trước, ví dụ Login
                        } else {
                            Toast.makeText(this, "Không thể gửi email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
