package models;

public class Order {
    private final int orderId;
    private String customerName;
    private String orderDetails;
    private double totalAmount;
    private String status;
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryStatus;

    public Order(int orderId, String customerName, String orderDetails,
                 double totalAmount, String status, String deliveryAddress,
                 String deliveryPhone, String deliveryStatus) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderDetails = orderDetails;
        this.totalAmount = totalAmount;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
        this.deliveryPhone = deliveryPhone;
        this.deliveryStatus = deliveryStatus;
    }

    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getOrderDetails() { return orderDetails; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getDeliveryPhone() { return deliveryPhone; }
    public String getDeliveryStatus() { return deliveryStatus; }

    public void setCustomerName(String value) { customerName = value; }
    public void setOrderDetails(String value) { orderDetails = value; }
    public void setTotalAmount(double value) { totalAmount = value; }
    public void setStatus(String value) { status = value; }
    public void setDeliveryAddress(String value) { deliveryAddress = value; }
    public void setDeliveryPhone(String value) { deliveryPhone = value; }
    public void setDeliveryStatus(String value) { deliveryStatus = value; }
}
