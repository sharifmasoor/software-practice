package screens;

import controller.OrderController;
import data.Store;
import models.Booking;
import models.CartItem;
import ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class BookOrderScreen extends JPanel {
    private final JTextField nameField = new JTextField(18);
    private final JTextField contactField = new JTextField(18);
    private final JTextArea addressArea = new JTextArea(2, 18);
    private final JTextField dateField = new JTextField(18);
    private final JComboBox<String> timeBox = new JComboBox<String>(new String[]{
            "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "01:00 PM",
            "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM", "06:00 PM"});
    private final JTextField notesField = new JTextField(18);
    private final DefaultTableModel cartModel;
    private final DefaultTableModel historyModel;
    private final JLabel totalLabel = new JLabel("Total: Rs. 0.00");
    private final Runnable onDataChanged;
    private final Runnable goToPayment;

    public BookOrderScreen(Runnable onDataChanged, Runnable goToPayment) {
        this.onDataChanged = onDataChanged;
        this.goToPayment = goToPayment;
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(Theme.heading("Book Order"), BorderLayout.NORTH);

        // ----- form (left) -----
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setOpaque(false);
        form.add(new JLabel("Customer name:"));            form.add(nameField);
        form.add(new JLabel("Contact (11 digits):"));      form.add(contactField);
        form.add(new JLabel("Delivery address:"));         form.add(new JScrollPane(addressArea));
        form.add(new JLabel("Date (DD-MM-YYYY):"));        form.add(dateField);
        form.add(new JLabel("Delivery time:"));            form.add(timeBox);
        form.add(new JLabel("Instructions (optional):"));  form.add(notesField);
        JPanel formBox = new JPanel(new BorderLayout());
        formBox.setOpaque(false);
        formBox.setBorder(BorderFactory.createTitledBorder("Customer and Delivery Details"));
        formBox.add(form, BorderLayout.NORTH);

        // ----- cart summary (right) -----
        cartModel = new DefaultTableModel(new Object[]{"Product", "Qty", "Subtotal (Rs.)"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable cartTable = new JTable(cartModel);
        Theme.styleTable(cartTable);
        JPanel summary = new JPanel(new BorderLayout(0, 6));
        summary.setOpaque(false);
        summary.setBorder(BorderFactory.createTitledBorder("Order Summary"));
        summary.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        summary.add(totalLabel, BorderLayout.SOUTH);

        JPanel upper = new JPanel(new GridLayout(1, 2, 10, 0));
        upper.setOpaque(false);
        upper.add(formBox);
        upper.add(summary);

        // ----- booking history (bottom) -----
        historyModel = new DefaultTableModel(new Object[]{"Booking ID", "Customer", "Items",
                "Total (Rs.)", "Date", "Time", "Status"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable historyTable = new JTable(historyModel);
        Theme.styleTable(historyTable);
        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyScroll.setBorder(BorderFactory.createTitledBorder("Booked Orders"));

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 10));
        center.setOpaque(false);
        center.add(upper);
        center.add(historyScroll);
        add(center, BorderLayout.CENTER);

        // ----- buttons -----
        JButton check = Theme.button("Check Availability");
        JButton book = Theme.button("Book Order");
        JButton clear = Theme.button("Clear Form");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(check); actions.add(book); actions.add(clear);
        add(actions, BorderLayout.SOUTH);

        check.addActionListener(e -> checkAvailability());
        book.addActionListener(e -> bookOrder());
        clear.addActionListener(e -> clearForm());
        clearForm();
        refreshData();
    }

    public void refreshData() {
        cartModel.setRowCount(0);
        for (CartItem item : Store.getCart()) {
            cartModel.addRow(new Object[]{item.getProduct().getName(), item.getQuantity(),
                    String.format("%.2f", item.getSubtotal())});
        }
        totalLabel.setText(String.format("Total: Rs. %.2f", OrderController.cartTotal()));

        historyModel.setRowCount(0);
        for (Booking b : Store.getBookings()) {
            historyModel.addRow(new Object[]{b.getBookingId(), b.getCustomerName(), b.getItems(),
                    String.format("%.2f", b.getTotal()), b.getDate(), b.getTime(), b.getStatus()});
        }
    }

    private void checkAvailability() {
        if (Store.getCart().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty. Add products first.",
                    "Empty cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<String> problems = OrderController.checkAvailability();
        if (problems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All products in your order are available.",
                    "Available", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Not enough stock for: " + String.join(", ", problems),
                    "Not available", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void bookOrder() {
        try {
            String time = (String) timeBox.getSelectedItem();
            double total = OrderController.cartTotal();
            Booking b = OrderController.bookOrder(nameField.getText(), contactField.getText(),
                    addressArea.getText(), dateField.getText(), time, notesField.getText());
            clearForm();
            refreshData();
            onDataChanged.run();
            int go = JOptionPane.showConfirmDialog(this, "Your order has been booked.\nBooking ID: " + b.getBookingId()
                    + String.format("\nTotal: Rs. %.2f", total) + "\nDelivery: " + b.getDate() + " at " + b.getTime()
                    + "\n\nGo to Make Payment now?",
                    "Booking successful", JOptionPane.YES_NO_OPTION);
            if (go == JOptionPane.YES_OPTION) goToPayment.run();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid information", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        nameField.setText("");
        contactField.setText("");
        addressArea.setText("");
        notesField.setText("");
        dateField.setText(LocalDate.now().plusDays(1).format(OrderController.DATE_FORMAT));
        timeBox.setSelectedIndex(0);
    }
}
