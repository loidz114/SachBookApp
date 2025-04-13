package com.example.sachbook;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import Model.Order;

public class OrderActivity extends AppCompatActivity {

    private EditText etFullName, etPhoneNumber, etAddress;
    private RecyclerView recyclerViewOrderItems;
    private TextView tvTotalPrice;
    private RadioGroup rgPaymentMethod;
    private Button btnConfirmOrder, btnBack; // Đảm bảo btnBack được khai báo
    private List<CartItem> cartItems;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Ánh xạ các thành phần giao diện
        etFullName = findViewById(R.id.etFullName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        btnBack = findViewById(R.id.btnBack); // Ánh xạ btnBack

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Nhận dữ liệu từ CartFragment
        cartItems = getIntent().getParcelableArrayListExtra("cartItems");
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0);

        // Kiểm tra cartItems có null hay không
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        // Hiển thị danh sách sản phẩm
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter cartAdapter = new CartAdapter(cartItems, null); // null vì không cần listener ở đây
        cartAdapter.setReadOnly(true); // Đặt adapter ở chế độ chỉ đọc
        recyclerViewOrderItems.setAdapter(cartAdapter);

        // Hiển thị tổng tiền
        tvTotalPrice.setText("Tổng tiền: " + totalPrice + " VNĐ");

        // Xử lý nút xác nhận đơn hàng
        btnConfirmOrder.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String paymentMethod = getSelectedPaymentMethod();

            // Log thông tin nhập vào để kiểm tra
            Log.d("OrderActivity", "FullName: " + fullName);
            Log.d("OrderActivity", "PhoneNumber: " + phoneNumber);
            Log.d("OrderActivity", "Address: " + address);

            // Kiểm tra thông tin nhập vào
            if (fullName.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đơn hàng và lưu vào lịch sử
            Order order = new Order(System.currentTimeMillis(), cartItems, totalPrice, fullName, phoneNumber, address, paymentMethod);
            HistoryManager.getInstance().addOrder(order);

            // Log để kiểm tra
            Log.d("OrderActivity", "Đơn hàng đã được lưu: " + order.getOrderId());
            Log.d("OrderActivity", "Số lượng đơn hàng trong HistoryManager: " + HistoryManager.getInstance().getOrderHistory().size());

            // Hiển thị thông báo đặt hàng thành công
            Toast.makeText(this, "Đặt hàng thành công!\nPhương thức thanh toán: " + paymentMethod, Toast.LENGTH_LONG).show();

            // Trả về kết quả cho CartFragment
            setResult(RESULT_OK);
            finish();
        });
    }

    // Lấy phương thức thanh toán được chọn
    private String getSelectedPaymentMethod() {
        int checkedId = rgPaymentMethod.getCheckedRadioButtonId();
        if (checkedId == R.id.rbCOD) {
            return "Thanh toán khi nhận hàng (COD)";
        } else if (checkedId == R.id.rbBankTransfer) {
            return "Chuyển khoản ngân hàng";
        } else if (checkedId == R.id.rbEWallet) {
            return "Ví điện tử (Momo, ZaloPay)";
        }
        return "Thanh toán khi nhận hàng (COD)"; // Mặc định
    }
}