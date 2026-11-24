package screens;

import controller.OrderController;
import data.Store;
import models.CartItem;
import ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PlaceOrderScreen extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel totalLabel;
    private final Runnable onDataChanged;

    public PlaceOrderScreen(Runnable onDataChanged, Runnable goToBooking) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(Theme.heading("Place Order - Review Your Cart"), BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Product", "Category", "Quantity", "Unit Price (Rs.)", "Subtotal (Rs.)"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton plus = Theme.button("+ 1");
        JButton minus = Theme.button("- 1");
        JButton remove = Theme.button("Remove");
        JButton clear = Theme.button("Clear Cart");
        JButton next = Theme.button("Go to Book Order");
        totalLabel = new JLabel("Total: Rs. 0.00");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(plus); left.add(minus); left.add(remove); left.add(clear);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(totalLabel); right.add(next);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(left, BorderLayout.WEST);
        bottom.add(right, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        plus.addActionListener(e -> change(1));
        minus.addActionListener(e -> change(-1));
        remove.addActionListener(e -> change(-100000));
        clear.addActionListener(e -> clearCart());
        next.addActionListener(e -> {
            if (Store.getCart().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Your cart is empty. Add products first.",
                        "Empty cart", JOptionPane.WARNING_MESSAGE);
                return;
            }
            goToBooking.run();
        });
        refreshData();
    }

    public void refreshData() {
        model.setRowCount(0);
        for (CartItem item : Store.getCart()) {
            model.addRow(new Object[]{item.getProduct().getName(), item.getProduct().getCategory(),
                    item.getQuantity(), String.format("%.2f", item.getProduct().getPrice()),
                    String.format("%.2f", item.getSubtotal())});
        }
        totalLabel.setText(String.format("Total: Rs. %.2f", OrderController.cartTotal()));
    }

    private void change(int delta) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product in the cart first.",
                    "No product selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CartItem item = Store.getCart().get(table.convertRowIndexToModel(viewRow));
        String problem = OrderController.changeQuantity(item, delta);
        if (problem != null)
            JOptionPane.showMessageDialog(this, problem, "Cannot change", JOptionPane.WARNING_MESSAGE);
        refreshData();
        onDataChanged.run();
    }

    private void clearCart() {
        if (Store.getCart().isEmpty()) return;
        int answer = JOptionPane.showConfirmDialog(this, "Remove everything from the cart?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            Store.getCart().clear();
            refreshData();
            onDataChanged.run();
        }
    }
}
