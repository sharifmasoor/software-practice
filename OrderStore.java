package data;

import java.util.ArrayList;
import java.util.List;
import models.Order;

/** Shared in-memory order data used by all three screens. */
public final class OrderStore {
    private static final List<Order> ORDERS = new ArrayList<Order>();

    static {
        ORDERS.add(new Order(101, "Ali Khan", "Chocolate Cake x 1", 2500,
                "Pending", "House 12, Islamabad", "03001234567", "Not Scheduled"));
        ORDERS.add(new Order(102, "Ahmed Raza", "Bread x 2, Cookies x 4", 1800,
                "Processing", "G-10, Islamabad", "03111234567", "Preparing"));
        ORDERS.add(new Order(103, "Sara Noor", "Birthday Cake x 1", 3500,
                "Completed", "F-8, Islamabad", "03221234567", "Delivered"));
        ORDERS.add(new Order(104, "Ayesha Malik", "Donuts x 6", 1200,
                "Pending", "", "", "Not Scheduled"));
        ORDERS.add(new Order(105, "Usman Ali", "Cupcakes x 12", 2200,
                "Completed", "I-8, Islamabad", "03331234567", "Out for Delivery"));
    }

    private OrderStore() { }

    public static List<Order> getOrders() {
        return ORDERS;
    }

    public static Order findById(int id) {
        for (Order order : ORDERS) {
            if (order.getOrderId() == id) return order;
        }
        return null;
    }
}
