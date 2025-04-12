package com.example.sachbook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etAddress, etDay, etMonth, etYear, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase; // Firebase Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etDay = findViewById(R.id.etDay);
        etMonth = findViewById(R.id.etMonth);
        etYear = findViewById(R.id.etYear);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        // Lấy dữ liệu
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String day = etDay.getText().toString().trim();
        String month = etMonth.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(day) || TextUtils.isEmpty(month) || TextUtils.isEmpty(year) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra email hợp lệ
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra độ dài mật khẩu
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra điều khoản
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng chấp nhận Điều khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra độ tuổi
        int birthDay = Integer.parseInt(day);
        int birthMonth = Integer.parseInt(month);
        int birthYear = Integer.parseInt(year);
        if (!isValidAge(birthDay, birthMonth, birthYear)) {
            Toast.makeText(this, "Bạn phải đủ 18 tuổi để đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        // Đăng ký Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        String uid = firebaseUser.getUid();

                        // Lưu thông tin bổ sung vào Realtime Database
                        HashMap<String, String> userMap = new HashMap<>();
                        userMap.put("fullName", fullName);
                        userMap.put("email", email);
                        userMap.put("phone", phone);
                        userMap.put("address", address);
                        userMap.put("dob", day + "/" + month + "/" + year);

                        mDatabase.child(uid).setValue(userMap)
                                .addOnCompleteListener(dbTask -> {
                                    if (dbTask.isSuccessful()) {
                                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                        // Chuyển sang màn hình đăng nhập hoặc màn hình chính sau khi đăng ký thành công
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish(); // Đóng màn hình đăng ký để người dùng không quay lại được
                                    } else {
                                        Toast.makeText(this, "Lưu dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Kiểm tra tuổi
    private boolean isValidAge(int day, int month, int year) {
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);
        int currentMonth = currentCalendar.get(Calendar.MONTH) + 1; // Tháng tính từ 0, vì vậy phải cộng 1
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);

        // Tính toán tuổi
        int age = currentYear - year;
        if (currentMonth < month || (currentMonth == month && currentDay < day)) {
            age--;
        }

        return age >= 18;
    }
}
