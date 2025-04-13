package com.example.sachbook;

import java.util.ArrayList;
import java.util.List;

import Model.Order;

public class HistoryManager {
    private static HistoryManager instance;
    private List<Order> orderHistory;

    private HistoryManager() {
        orderHistory = new ArrayList<>();
    }

    public static synchronized HistoryManager getInstance() {
        if (instance == null) {
            instance = new HistoryManager();
        }
        return instance;
    }

    public void addOrder(Order order) {
        orderHistory.add(order);
    }

    public List<Order> getOrderHistory() {
        return new ArrayList<>(orderHistory);
    }

    public void clearHistory() {
        orderHistory.clear();
    }
}