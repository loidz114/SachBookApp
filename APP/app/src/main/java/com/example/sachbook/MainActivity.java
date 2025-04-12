package com.example.sachbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewBooks;
    private BottomNavigationView bottomNavigationView;
    private EditText etSearch;
    private Button btnSearch, btnFiction, btnScience, btnHistory;
    private LinearLayout homeContent;
    private SimpleAdapter simpleAdapter;
    private List<String> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ánh xạ các thành phần giao diện
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnFiction = findViewById(R.id.btnFiction);
        btnScience = findViewById(R.id.btnScience);
        btnHistory = findViewById(R.id.btnHistory);
        homeContent = findViewById(R.id.homeContent);

        // Khởi tạo danh sách mục (dữ liệu giả)
        itemList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            itemList.add("Item " + i);
        }

        // Thiết lập RecyclerView (danh sách sách)
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
        simpleAdapter = new SimpleAdapter(itemList);
        recyclerViewBooks.setAdapter(simpleAdapter);

        // Hiển thị trang chủ mặc định
        showHome();

        // Xử lý sự kiện BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_cart) {
                // Hiển thị CartFragment
                loadFragment(new CartFragment());
                homeContent.setVisibility(View.GONE);
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                return true;
            } else if (itemId == R.id.nav_home) {
                // Hiển thị trang chủ
                showHome();
                return true;
//            } else if (itemId == R.id.nav_favourites) {
//                // Hiển thị FavouritesFragment (nếu có)
//                loadFragment(new FavouritesFragment());
//                homeContent.setVisibility(View.GONE);
//                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
//                return true;
            } else if (itemId == R.id.nav_history) {
                // Hiển thị HistoryFragment
                loadFragment(new HistoryFragment());
                homeContent.setVisibility(View.GONE);
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                return true;
            } else if (itemId == R.id.nav_settings) {
                // Hiển thị SettingsFragment (nếu có)
                // loadFragment(new SettingsFragment());
                homeContent.setVisibility(View.GONE);
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                return true;
            }
            return false;
        });

        // Đặt mục "Trang chủ" được chọn mặc định
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Xử lý sự kiện nút tìm kiếm
        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString();
            Toast.makeText(MainActivity.this, "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
        });

        // Xử lý sự kiện các nút phân loại
        btnFiction.setOnClickListener(v -> filterBooks("Tiểu thuyết"));
        btnScience.setOnClickListener(v -> filterBooks("Khoa học"));
        btnHistory.setOnClickListener(v -> filterBooks("Lịch sử"));

        // Xử lý WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Hàm để load Fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Hàm để hiển thị trang chủ
    private void showHome() {
        homeContent.setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_container).setVisibility(View.GONE);
    }

    // Hàm lọc sách (hiện tại chỉ hiển thị Toast)
    private void filterBooks(String category) {
        Toast.makeText(this, "Lọc sách theo: " + category, Toast.LENGTH_SHORT).show();
    }
}