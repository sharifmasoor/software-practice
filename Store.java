package data;

import java.util.ArrayList;
import java.util.List;
import models.Booking;
import models.CartItem;
import models.Payment;
import models.Product;
import models.Salesman;

/** Shared in-memory data used by all three screens. */
public final class Store {
    private static final List<Product> PRODUCTS = new ArrayList<Product>();
    private static final List<CartItem> CART = new ArrayList<CartItem>();
    private static final List<Booking> BOOKINGS = new ArrayList<Booking>();
    private static final List<Payment> PAYMENTS = new ArrayList<Payment>();
    private static final List<Salesman> SALESMEN = new ArrayList<Salesman>();
    private static int nextBookingNumber = 1001;
    private static int nextPaymentNumber = 2001;

    static {
        PRODUCTS.add(new Product(1, "Chocolate Cake", "Cakes", 1500, 10, "A delicious chocolate cake made with rich chocolate."));
        PRODUCTS.add(new Product(2, "Vanilla Cake", "Cakes", 1200, 10, "A soft and fresh vanilla flavoured cake."));
        PRODUCTS.add(new Product(3, "Strawberry Cake", "Cakes", 1800, 8, "A tasty cake topped with fresh strawberries."));
        PRODUCTS.add(new Product(4, "Cupcake", "Snacks", 200, 50, "A small and sweet cupcake for any occasion."));
        PRODUCTS.add(new Product(5, "Chocolate Donut", "Snacks", 150, 40, "A soft donut covered with chocolate."));
        PRODUCTS.add(new Product(6, "Brownie", "Snacks", 300, 30, "A soft chocolate brownie with a rich taste."));
        PRODUCTS.add(new Product(7, "Fresh Bread", "Bread", 180, 25, "Freshly baked bread for everyday use."));
        PRODUCTS.add(new Product(8, "Milk Bread", "Bread", 220, 25, "Soft and delicious bread made with milk."));
    }

    static {
        SALESMEN.add(new Salesman(1, "Ali Raza", "03001234567", "Gulberg"));
        SALESMEN.add(new Salesman(2, "Usman Khan", "03111234567", "Saddar"));
        SALESMEN.add(new Salesman(3, "Bilal Ahmed", "03211234567", "University Town"));
    }

    private Store() { }

    public static List<Product> getProducts() { return PRODUCTS; }
    public static List<CartItem> getCart() { return CART; }
    public static List<Booking> getBookings() { return BOOKINGS; }
    public static String newBookingId() { return "BK" + (nextBookingNumber++); }
    public static List<Payment> getPayments() { return PAYMENTS; }
    public static List<Salesman> getSalesmen() { return SALESMEN; }
    public static String newPaymentId() { return "PY" + (nextPaymentNumber++); }
}
