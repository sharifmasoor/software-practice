package screens;

import data.OrderStore;
import models.Order;
import ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageSalesScreen extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JLabel totalLabel;

    public ManageSalesScreen() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(Theme.heading("Manage Sales"), BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Order ID", "Customer", "Items", "Sale Amount (Rs.)"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        totalLabel = new JLabel("Total completed sales: Rs. 0.00");
        totalLabel.setForeground(Theme.TEXT);

        JButton details = Theme.button("View Sale Details");
        JButton refresh = Theme.button("Refresh");
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(totalLabel, BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(details); buttons.add(refresh);
        bottom.add(buttons, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        details.addActionListener(e -> viewSale());
        refresh.addActionListener(e -> refreshData());
        refreshData();
    }

    public void refreshData() {
        model.setRowCount(0);
        double total = 0;
        int count = 0;
        for (Order o : OrderStore.getOrders()) {
            if ("Completed".equals(o.getStatus())) {
                model.addRow(new Object[]{o.getOrderId(), o.getCustomerName(),
                        o.getOrderDetails(), String.format("%.2f", o.getTotalAmount())});
                total += o.getTotalAmount();
                count++;
            }
        }
        if (count == 0) {
            model.addRow(new Object[]{"-", "No sales records found", "-", "-"});
        }
        totalLabel.setText("Completed orders: " + count + "     |     Total sales: Rs. "
                + String.format("%.2f", total));
    }

    private void viewSale() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a sales record.",
                    "No record selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int row = table.convertRowIndexToModel(viewRow);
        Object idValue = model.getValueAt(row, 0);
        if (!(idValue instanceof Number)) {
            JOptionPane.showMessageDialog(this, "There are currently no sales records.");
            return;
        }
        Order o = OrderStore.findById(((Number) idValue).intValue());
        if (o != null) {
            JOptionPane.showMessageDialog(this,
                    "Sale record\n\nOrder ID: " + o.getOrderId()
                    + "\nCustomer: " + o.getCustomerName()
                    + "\nItems: " + o.getOrderDetails()
                    + "\nAmount: Rs. " + String.format("%.2f", o.getTotalAmount())
                    + "\nStatus: " + o.getStatus(),
                    "Sale Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
