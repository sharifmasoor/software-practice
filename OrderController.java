package controller;

import data.Store;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import models.Booking;
import models.CartItem;
import models.Product;

public final class OrderController {
    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private OrderController() { }

    public static List<Product> productsInCategory(String category) {
        List<Product> result = new ArrayList<Product>();
        for (Product p : Store.getProducts()) {
            if ("All".equals(category) || p.getCategory().equals(category)) result.add(p);
        }
        return result;
    }

    private static CartItem findInCart(Product p) {
        for (CartItem item : Store.getCart()) {
            if (item.getProduct() == p) return item;
        }
        return null;
    }

    public static String addToCart(Product p, int quantity) {
        if (p.getStock() <= 0) return p.getName() + " is out of stock.";
        CartItem existing = findInCart(p);
        int already = existing == null ? 0 : existing.getQuantity();
        if (already + quantity > p.getStock())
            return "Only " + p.getStock() + " x " + p.getName() + " available (" + already + " already in cart).";
        if (existing == null) Store.getCart().add(new CartItem(p, quantity));
        else existing.setQuantity(already + quantity);
        return null;
    }
    public static String changeQuantity(CartItem item, int delta) {
        int q = item.getQuantity() + delta;
        if (q <= 0) { Store.getCart().remove(item); return null; }
        if (q > item.getProduct().getStock())
            return "Only " + item.getProduct().getStock() + " x " + item.getProduct().getName() + " available.";
        item.setQuantity(q);
        return null;
    }

    public static double cartTotal() {
        double total = 0;
        for (CartItem item : Store.getCart()) total += item.getSubtotal();
        return total;
    }

    public static int cartCount() {
        int count = 0;
        for (CartItem item : Store.getCart()) count += item.getQuantity();
        return count;
    }

    public static List<String> checkAvailability() {
        List<String> problems = new ArrayList<String>();
        for (CartItem item : Store.getCart()) {
            if (item.getQuantity() > item.getProduct().getStock())
                problems.add(item.getProduct().getName() + " (only " + item.getProduct().getStock() + " left)");
        }
        return problems;
    }
    public static Booking bookOrder(String name, String contact, String address,
                                    String dateText, String time, String instructions) {
        if (Store.getCart().isEmpty())
            throw new IllegalArgumentException("Your cart is empty. Add products first.");
        if (name.trim().length() < 2 || !name.trim().matches("[A-Za-z .'-]+"))
            throw new IllegalArgumentException("Enter a valid customer name (letters only).");
        if (!contact.trim().matches("\\d{11}"))
            throw new IllegalArgumentException("Contact number must be 11 digits, e.g. 03001234567.");
        if (address.trim().length() < 5)
            throw new IllegalArgumentException("Enter the delivery address.");
        LocalDate date;
        try {
            date = LocalDate.parse(dateText.trim(), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Enter a valid date as DD-MM-YYYY, e.g. 25-11-2026.");
        }
        if (!date.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Booking date must be a future date.");
        List<String> problems = checkAvailability();
        if (!problems.isEmpty())
            throw new IllegalArgumentException("Not enough stock for: " + String.join(", ", problems));

        StringBuilder items = new StringBuilder();
        double total = 0;
        for (CartItem item : Store.getCart()) {
            if (items.length() > 0) items.append(", ");
            items.append(item.getProduct().getName()).append(" x ").append(item.getQuantity());
            total += item.getSubtotal();
            item.getProduct().setStock(item.getProduct().getStock() - item.getQuantity());
        }
        Booking booking = new Booking(Store.newBookingId(), name.trim(), contact.trim(), address.trim(),
                items.toString(), total, date.format(DATE_FORMAT), time, instructions.trim(), "Confirmed");
        Store.getBookings().add(booking);
        Store.getCart().clear();
        return booking;
    }
}
