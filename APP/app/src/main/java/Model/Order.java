package Model;

public class Order {
    private String orderId;
    private String orderDate;
    private double totalPrice;

    public Order(String orderId, String orderDate, double totalPrice) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
    }

    public String getOrderId() { return orderId; }
    public String getOrderDate() { return orderDate; }
    public double getTotalPrice() { return totalPrice; }
}
