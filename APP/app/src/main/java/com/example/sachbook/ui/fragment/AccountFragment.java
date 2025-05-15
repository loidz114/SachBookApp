package com.example.sachbook.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.sachbook.R;
import com.example.sachbook.data.model.UpdateProfileRequest;
import com.example.sachbook.data.model.UserModel;
import com.example.sachbook.data.remote.ApiClient;
import com.example.sachbook.data.remote.BookApiService;
import com.example.sachbook.ui.activity.LoginActivity;
import com.example.sachbook.ui.activity.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private TextInputEditText userName, userEmail, userPhone, userAddress;
    private MaterialButton editProfileButton, logoutButton;
    private BookApiService apiService;
    private boolean isEditing = false;
    private View rootView;
    private static final String PREFS_NAME = "SachBookPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long DEBOUNCE_DELAY = 500;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_account, container, false);

        userName = rootView.findViewById(R.id.userName);
        userEmail = rootView.findViewById(R.id.userEmail);
        userPhone = rootView.findViewById(R.id.userPhone);
        userAddress = rootView.findViewById(R.id.userAddress);
        editProfileButton = rootView.findViewById(R.id.editProfileButton);
        logoutButton = rootView.findViewById(R.id.logoutButton);

        apiService = ApiClient.getBookApiService(requireContext());

        setFieldsEditable(false);

        String token = getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return rootView;
        }

        loadUserInfo(token);

        editProfileButton.setOnClickListener(v -> {
            if (!isEditing) {
                isEditing = true;
                setFieldsEditable(true);
                editProfileButton.setText("Lưu");
            } else {
                saveProfile(token);
            }
        });

        logoutButton.setOnClickListener(v -> {
            logoutButton.setEnabled(false);
            handler.postDelayed(() -> logoutButton.setEnabled(true), DEBOUNCE_DELAY);
            new AlertDialog.Builder(requireContext())
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> logout(token))
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        return rootView;
    }

    private void setFieldsEditable(boolean editable) {
        userName.setEnabled(editable);
        userPhone.setEnabled(editable);
        userAddress.setEnabled(editable);
        userEmail.setEnabled(false);
    }

    private String getToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    private void loadUserInfo(String token) {
        if (!isAdded()) return;
        Toast.makeText(requireContext(), "Đang tải thông tin...", Toast.LENGTH_SHORT).show();
        apiService.getUserProfile("Bearer " + token).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    UserModel user = response.body();
                    userName.setText(user.getFullName() != null ? user.getFullName() : "");
                    userEmail.setText(user.getEmail() != null ? user.getEmail() : "");
                    userPhone.setText(user.getPhone() != null ? user.getPhone() : "");
                    userAddress.setText(user.getAddress() != null ? user.getAddress() : "");
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(requireContext(), "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                        redirectToLogin();
                    } else {
                        Toast.makeText(requireContext(), "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile(String token) {
        if (!isAdded()) return;
        String name = userName.getText().toString().trim();
        String phone = userPhone.getText().toString().trim();
        String address = userAddress.getText().toString().trim();

        if (name.isEmpty()) {
            userName.setError("Họ và tên không được để trống");
            return;
        }
        if (phone.isEmpty()) {
            userPhone.setError("Số điện thoại không được để trống");
            return;
        }
        if (!phone.matches("\\d{10,11}")) {
            userPhone.setError("Số điện thoại không hợp lệ");
            return;
        }

        editProfileButton.setEnabled(false);
        UpdateProfileRequest request = new UpdateProfileRequest(name, phone, address);
        apiService.updateProfile("Bearer " + token, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;
                editProfileButton.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    isEditing = false;
                    setFieldsEditable(false);
                    editProfileButton.setText("Chỉnh sửa thông tin");
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(requireContext(), "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                        redirectToLogin();
                    } else {
                        Toast.makeText(requireContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                editProfileButton.setEnabled(true);
                Toast.makeText(requireContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout(String token) {
        if (!isAdded()) return;
        apiService.logout("Bearer " + token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!isAdded()) return;
                clearToken();
                Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                redirectToMain();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (!isAdded()) return;
                clearToken();
                Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                redirectToMain();
            }
        });
    }

    private void clearToken() {
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_TOKEN)
                .apply();
    }

    private void redirectToMain() {
        if (!isAdded()) return;
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void redirectToLogin() {
        if (!isAdded()) return;
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}