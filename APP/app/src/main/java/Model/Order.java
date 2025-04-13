package Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.sachbook.CartItem;

import java.util.ArrayList;
import java.util.List;

public class Order implements Parcelable {
    private long orderId; // ID đơn hàng (dùng thời gian đặt hàng)
    private List<CartItem> items; // Danh sách sản phẩm
    private double totalPrice; // Tổng tiền
    private String fullName; // Họ tên người nhận
    private String phoneNumber; // Số điện thoại
    private String address; // Địa chỉ giao hàng
    private String paymentMethod; // Phương thức thanh toán

    public Order(long orderId, List<CartItem> items, double totalPrice, String fullName, String phoneNumber, String address, String paymentMethod) {
        this.orderId = orderId;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.totalPrice = totalPrice;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    protected Order(Parcel in) {
        orderId = in.readLong();
        items = in.createTypedArrayList(CartItem.CREATOR);
        totalPrice = in.readDouble();
        fullName = in.readString();
        phoneNumber = in.readString();
        address = in.readString();
        paymentMethod = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(orderId);
        dest.writeTypedList(items);
        dest.writeDouble(totalPrice);
        dest.writeString(fullName);
        dest.writeString(phoneNumber);
        dest.writeString(address);
        dest.writeString(paymentMethod);
    }

    // Getters
    public long getOrderId() {
        return orderId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
