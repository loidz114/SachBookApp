package com.example.sachbook;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ánh xạ các thành phần giao diện
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Thiết lập RecyclerView (danh sách sách)
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
        // Bạn cần thêm adapter cho recyclerViewBooks ở đây nếu chưa có
        // Ví dụ: recyclerViewBooks.setAdapter(new BookAdapter(bookList));

        // Xử lý sự kiện BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_cart) {
                // Chuyển sang CartActivity khi nhấn vào "Giỏ hàng"
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_home) {
                // Đã ở trang chủ, không cần làm gì
                return true;
//            } else if (itemId == R.id.nav_favourites) {
//                // Xử lý mục "Yêu thích" (nếu cần)
//                // Ví dụ: startActivity(new Intent(MainActivity.this, FavouritesActivity.class));
//                return true;
            } else if (itemId == R.id.nav_settings) {
                // Xử lý mục "Cài đặt" (nếu cần)
                // Ví dụ: startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            }
            return false;
        });

        // Đặt mục "Trang chủ" được chọn mặc định
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Xử lý WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}