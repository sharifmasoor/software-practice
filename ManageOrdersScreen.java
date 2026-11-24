package screens;

import data.OrderStore;
import models.Order;
import ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageOrdersScreen extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final Runnable onDataChanged;

    public ManageOrdersScreen(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(Theme.heading("Manage Orders"), BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Order ID", "Customer", "Order Details", "Amount (Rs.)", "Status"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton view = Theme.button("View Details");
        JButton update = Theme.button("Update Order");
        JButton cancel = Theme.button("Cancel Order");
        JButton refresh = Theme.button("Refresh");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(view);
        actions.add(update);
        actions.add(cancel);
        actions.add(refresh);
        add(actions, BorderLayout.SOUTH);

        view.addActionListener(e -> viewOrder());
        update.addActionListener(e -> updateOrder());
        cancel.addActionListener(e -> cancelOrder());
        refresh.addActionListener(e -> refreshData());
        refreshData();
    }

    public void refreshData() {
        model.setRowCount(0);
        for (Order o : OrderStore.getOrders()) {
            model.addRow(new Object[]{o.getOrderId(), o.getCustomerName(),
                    o.getOrderDetails(), String.format("%.2f", o.getTotalAmount()), o.getStatus()});
        }
    }

    private Order selectedOrder() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an order first.",
                    "No order selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int row = table.convertRowIndexToModel(viewRow);
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        return OrderStore.findById(id);
    }

    private void viewOrder() {
        Order o = selectedOrder();
        if (o == null) return;
        String details = "Order ID: " + o.getOrderId()
                + "\nCustomer: " + o.getCustomerName()
                + "\nItems: " + o.getOrderDetails()
                + "\nTotal: Rs. " + String.format("%.2f", o.getTotalAmount())
                + "\nOrder status: " + o.getStatus()
                + "\nDelivery status: " + o.getDeliveryStatus();
        JOptionPane.showMessageDialog(this, details, "Order Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateOrder() {
        Order o = selectedOrder();
        if (o == null) return;
        if ("Cancelled".equals(o.getStatus())) {
            JOptionPane.showMessageDialog(this, "A cancelled order cannot be updated.",
                    "Unavailable order", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField customer = new JTextField(o.getCustomerName(), 18);
        JTextField items = new JTextField(o.getOrderDetails(), 18);
        JTextField amount = new JTextField(String.valueOf(o.getTotalAmount()), 18);
        JComboBox<String> status = new JComboBox<String>(
                new String[]{"Pending", "Processing", "Completed"});
        status.setSelectedItem(o.getStatus());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Customer name:")); form.add(customer);
        form.add(new JLabel("Order details:")); form.add(items);
        form.add(new JLabel("Total amount:")); form.add(amount);
        form.add(new JLabel("Order status:")); form.add(status);

        int result = JOptionPane.showConfirmDialog(this, form, "Update Order #" + o.getOrderId(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String name = customer.getText().trim();
        String description = items.getText().trim();
        double price;
        try {
            price = Double.parseDouble(amount.getText().trim());
            if (name.isEmpty() || description.isEmpty() || price < 0) throw new IllegalArgumentException();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Enter a customer, order details and a valid non-negative amount.",
                    "Invalid information", JOptionPane.ERROR_MESSAGE);
            return;
        }

        o.setCustomerName(name);
        o.setOrderDetails(description);
        o.setTotalAmount(price);
        o.setStatus((String) status.getSelectedItem());
        refreshData();
        onDataChanged.run();
        JOptionPane.showMessageDialog(this, "Order updated successfully.",
                "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelOrder() {
        Order o = selectedOrder();
        if (o == null) return;
        if ("Completed".equals(o.getStatus())) {
            JOptionPane.showMessageDialog(this, "Completed orders cannot be cancelled.",
                    "Cannot cancel", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if ("Cancelled".equals(o.getStatus())) {
            JOptionPane.showMessageDialog(this, "This order is already cancelled.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel order #" + o.getOrderId() + "?",
                "Confirm cancellation", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            o.setStatus("Cancelled");
            o.setDeliveryStatus("Cancelled");
            refreshData();
            onDataChanged.run();
            JOptionPane.showMessageDialog(this, "Order cancelled successfully.");
        }
    }
}
