package screens;

import controller.OrderController;
import models.Booking;
import models.Payment;
import ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MakePaymentScreen extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel amountLabel = new JLabel("Select a booking above");
    private final JTextField payerField = new JTextField(18);
    private final JComboBox<String> methodBox = new JComboBox<String>(OrderController.PAYMENT_METHODS);
    private final JTextField referenceField = new JTextField(18);
    private final Runnable onDataChanged;
    private List<Booking> shown = new ArrayList<Booking>();

    public MakePaymentScreen(Runnable onDataChanged, Runnable goToProcess) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(Theme.heading("Make Payment"), BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Booking ID", "Customer", "Items",
                "Total (Rs.)", "Delivery Date", "Payment Status"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Bookings Awaiting Payment"));

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.setOpaque(false);
        form.add(new JLabel("Amount to pay:"));            form.add(amountLabel);
        form.add(new JLabel("Payer name:"));               form.add(payerField);
        form.add(new JLabel("Payment method:"));           form.add(methodBox);
        form.add(new JLabel("Transaction / reference no.:")); form.add(referenceField);
        JPanel formBox = new JPanel(new BorderLayout());
        formBox.setOpaque(false);
        formBox.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        formBox.add(form, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 10));
        center.setOpaque(false);
        center.add(tableScroll);
        center.add(formBox);
        add(center, BorderLayout.CENTER);

        JButton pay = Theme.button("Make Payment");
        JButton clear = Theme.button("Clear Form");
        JButton next = Theme.button("Go to Process Payment");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(pay); actions.add(clear); actions.add(next);
        add(actions, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            Booking b = selectedBooking();
            amountLabel.setText(b == null ? "Select a booking above" : String.format("Rs. %.2f", b.getTotal()));
        });
        methodBox.addActionListener(e -> {
            boolean cash = String.valueOf(methodBox.getSelectedItem()).startsWith("Cash");
            referenceField.setEnabled(!cash);
            if (cash) referenceField.setText("");
        });
        pay.addActionListener(e -> makePayment());
        clear.addActionListener(e -> clearForm());
        next.addActionListener(e -> goToProcess.run());
        refreshData();
    }

    public void refreshData() {
        shown = OrderController.bookingsAwaitingPayment();
        model.setRowCount(0);
        for (Booking b : shown) {
            model.addRow(new Object[]{b.getBookingId(), b.getCustomerName(), b.getItems(),
                    String.format("%.2f", b.getTotal()), b.getDate(), b.getPaymentStatus()});
        }
        amountLabel.setText("Select a booking above");
    }

    private Booking selectedBooking() {
        int viewRow = table.getSelectedRow();
        return viewRow < 0 ? null : shown.get(table.convertRowIndexToModel(viewRow));
    }

    private void makePayment() {
        try {
            Payment p = OrderController.makePayment(selectedBooking(), payerField.getText(),
                    (String) methodBox.getSelectedItem(), referenceField.getText());
            clearForm();
            refreshData();
            onDataChanged.run();
            JOptionPane.showMessageDialog(this, "Payment submitted for approval.\nPayment ID: " + p.getPaymentId()
                    + "\nBooking: " + p.getBookingId() + String.format("\nAmount: Rs. %.2f", p.getAmount()),
                    "Payment submitted", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid information", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        payerField.setText("");
        referenceField.setText("");
        methodBox.setSelectedIndex(0);
        table.clearSelection();
    }
}
