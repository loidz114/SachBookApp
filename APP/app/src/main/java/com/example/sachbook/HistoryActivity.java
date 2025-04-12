package com.example.sachbook;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import Model.Order;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Ánh xạ RecyclerView
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);

        // Khởi tạo danh sách lịch sử mua hàng (dữ liệu giả)
        orderList = new ArrayList<>();
        orderList.add(new Order("DH001", "2025-04-10", 150000));
        orderList.add(new Order("DH002", "2025-04-11", 220000));

        // Thiết lập RecyclerView
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(orderList);
        recyclerViewHistory.setAdapter(historyAdapter);
    }
}