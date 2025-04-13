package com.example.sachbook;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import Model.Order;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerViewHistory;
    private HistoryAdapter historyAdapter;
    private List<Order> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);

        // Lấy danh sách đơn hàng từ HistoryManager
        orderList = HistoryManager.getInstance().getOrderHistory();
        if (orderList == null) {
            orderList = new ArrayList<>();
        }

        // Log để kiểm tra
        Log.d("HistoryFragment", "Số lượng đơn hàng khi tạo: " + orderList.size());

        // Thiết lập RecyclerView
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryAdapter(orderList);
        recyclerViewHistory.setAdapter(historyAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại danh sách khi fragment được hiển thị
        orderList = HistoryManager.getInstance().getOrderHistory();
        Log.d("HistoryFragment", "Số lượng đơn hàng khi resume: " + orderList.size());
        historyAdapter.updateData(orderList);
    }
}