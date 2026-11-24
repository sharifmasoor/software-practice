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
import models.Payment;
import models.Product;
import models.Salesman;

/** Receives data from the screens, checks it, and updates the shared store. */
public final class OrderController {
    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    private OrderController() { }

    // ---------- View Products ----------
    public static List<Product> productsInCategory(String category) {
        List<Product> result = new ArrayList<Product>();
        for (Product p : Store.getProducts()) {
            if ("All".equals(category) || p.getCategory().equals(category)) result.add(p);
        }
        return result;
    }

    // ---------- Place Order (cart) ----------
    private static CartItem findInCart(Product p) {
        for (CartItem item : Store.getCart()) {
            if (item.getProduct() == p) return item;
        }
        return null;
    }

    /** Returns null when added, otherwise the reason it could not be added. */
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

    /** Returns null when done, otherwise the reason it could not be changed. */
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

    // ---------- Book Order ----------
    /** Returns a list of problems; empty means everything is available. */
    public static List<String> checkAvailability() {
        List<String> problems = new ArrayList<String>();
        for (CartItem item : Store.getCart()) {
            if (item.getQuantity() > item.getProduct().getStock())
                problems.add(item.getProduct().getName() + " (only " + item.getProduct().getStock() + " left)");
        }
        return problems;
    }

    /** Validates the details, books the order and returns it. Throws IllegalArgumentException with a message. */
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

    // ---------- Make Payment ----------
    public static final String[] PAYMENT_METHODS =
            {"Cash on Delivery", "Credit/Debit Card", "Easypaisa", "JazzCash", "Bank Transfer"};

    /** Bookings that still need a payment (never paid, or the last payment was rejected). */
    public static List<Booking> bookingsAwaitingPayment() {
        List<Booking> result = new ArrayList<Booking>();
        for (Booking b : Store.getBookings()) {
            if ("Unpaid".equals(b.getPaymentStatus()) || "Rejected".equals(b.getPaymentStatus())) result.add(b);
        }
        return result;
    }

    /** Validates the payment details and records a Pending payment. Throws IllegalArgumentException with a message. */
    public static Payment makePayment(Booking booking, String payerName, String method, String reference) {
        if (booking == null)
            throw new IllegalArgumentException("Select the booking you want to pay for.");
        if (!"Unpaid".equals(booking.getPaymentStatus()) && !"Rejected".equals(booking.getPaymentStatus()))
            throw new IllegalArgumentException("This booking already has a payment (" + booking.getPaymentStatus() + ").");
        if (payerName.trim().length() < 2 || !payerName.trim().matches("[A-Za-z .'-]+"))
            throw new IllegalArgumentException("Enter the payer name (letters only).");
        if (method == null)
            throw new IllegalArgumentException("Select a payment method.");
        boolean cash = method.startsWith("Cash");
        String ref = reference.trim();
        if (!cash && !ref.matches("[A-Za-z0-9]{6,20}"))
            throw new IllegalArgumentException("Enter the transaction / reference number (6 to 20 letters or digits).");
        Payment payment = new Payment(Store.newPaymentId(), booking.getBookingId(), payerName.trim(), method,
                cash ? "-" : ref, booking.getTotal(), LocalDate.now().format(DATE_FORMAT));
        Store.getPayments().add(payment);
        booking.setPaymentStatus("Pending");
        return payment;
    }

    // ---------- Process Payment ----------
    public static List<Payment> pendingPayments() {
        List<Payment> result = new ArrayList<Payment>();
        for (Payment p : Store.getPayments()) {
            if ("Pending".equals(p.getStatus())) result.add(p);
        }
        return result;
    }

    private static Booking findBooking(String bookingId) {
        for (Booking b : Store.getBookings()) {
            if (b.getBookingId().equals(bookingId)) return b;
        }
        return null;
    }

    /** Approves or rejects a pending payment and updates the booking's payment status. */
    public static void processPayment(Payment payment, boolean approve, String remarks) {
        if (payment == null)
            throw new IllegalArgumentException("Select a payment to process.");
        if (!"Pending".equals(payment.getStatus()))
            throw new IllegalArgumentException("This payment is already " + payment.getStatus().toLowerCase() + ".");
        if (!approve && remarks.trim().length() < 3)
            throw new IllegalArgumentException("Enter the reason (remarks) for rejecting the payment.");
        payment.setStatus(approve ? "Approved" : "Rejected");
        payment.setRemarks(remarks.trim());
        Booking booking = findBooking(payment.getBookingId());
        if (booking != null) booking.setPaymentStatus(approve ? "Paid" : "Rejected");
    }

    // ---------- Assign Salesman ----------
    public static final int MAX_ACTIVE_DELIVERIES = 5;

    public static List<Booking> paidBookings() {
        List<Booking> result = new ArrayList<Booking>();
        for (Booking b : Store.getBookings()) {
            if ("Paid".equals(b.getPaymentStatus())) result.add(b);
        }
        return result;
    }

    public static int activeDeliveries(Salesman salesman) {
        int count = 0;
        for (Booking b : Store.getBookings()) {
            if (salesman.getName().equals(b.getSalesman())) count++;
        }
        return count;
    }

    public static void assignSalesman(Booking booking, Salesman salesman) {
        if (booking == null)
            throw new IllegalArgumentException("Select a booking.");
        if (salesman == null)
            throw new IllegalArgumentException("Select a salesman.");
        if (!"Paid".equals(booking.getPaymentStatus()))
            throw new IllegalArgumentException("The payment must be approved before a salesman is assigned.");
        if (salesman.getName().equals(booking.getSalesman()))
            throw new IllegalArgumentException(salesman.getName() + " is already assigned to this booking.");
        if (activeDeliveries(salesman) >= MAX_ACTIVE_DELIVERIES)
            throw new IllegalArgumentException(salesman.getName() + " already has " + MAX_ACTIVE_DELIVERIES
                    + " deliveries. Choose another salesman.");
        booking.setSalesman(salesman.getName());
    }
}
