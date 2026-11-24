package data;

import java.util.ArrayList;
import java.util.List;
import models.Booking;
import models.CartItem;
import models.Product;

public final class Store {
    private static final List<Product> PRODUCTS = new ArrayList<Product>();
    private static final List<CartItem> CART = new ArrayList<CartItem>();
    private static final List<Booking> BOOKINGS = new ArrayList<Booking>();
    private static int nextBookingNumber = 1001;

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

    private Store() { }

    public static List<Product> getProducts() { return PRODUCTS; }
    public static List<CartItem> getCart() { return CART; }
    public static List<Booking> getBookings() { return BOOKINGS; }
    public static String newBookingId() { return "BK" + (nextBookingNumber++); }
}
