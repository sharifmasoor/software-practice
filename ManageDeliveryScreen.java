package screens;

import data.OrderStore;
import models.Order;
import ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageDeliveryScreen extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField addressField;
    private final JTextField phoneField;
    private final JComboBox<String> statusBox;

    public ManageDeliveryScreen() {
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(Theme.heading("Manage Delivery"), BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Order ID", "Customer", "Delivery Address", "Phone", "Delivery Status"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedOrder();
        });
        JScrollPane scroll = new JScrollPane(table);

        addressField = new JTextField();
        phoneField = new JTextField();
        statusBox = new JComboBox<String>(
                new String[]{"Not Scheduled", "Preparing", "Out for Delivery", "Delivered"});

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createTitledBorder("Delivery details"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        addField(form, c, 0, "Delivery address:", addressField);
        addField(form, c, 1, "Customer phone:", phoneField);
        addField(form, c, 2, "Delivery status:", statusBox);

        JButton save = Theme.button("Save");
        JButton refresh = Theme.button("Refresh");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(refresh); actions.add(save);

        JPanel bottom = new JPanel(new BorderLayout(0, 10));
        bottom.setOpaque(false);
        bottom.add(form, BorderLayout.CENTER);
        bottom.add(actions, BorderLayout.SOUTH);

        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        save.addActionListener(e -> saveDelivery());
        refresh.addActionListener(e -> refreshData());
        refreshData();
    }

    private void addField(JPanel panel, GridBagConstraints c, int row,
                          String label, JComponent field) {
        c.gridy = row;
        c.gridx = 0;
        c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(field, c);
    }

    public void refreshData() {
        model.setRowCount(0);
        for (Order o : OrderStore.getOrders()) {
            if (!"Cancelled".equals(o.getStatus())) {
                model.addRow(new Object[]{o.getOrderId(), o.getCustomerName(),
                        emptyText(o.getDeliveryAddress()), emptyText(o.getDeliveryPhone()),
                        o.getDeliveryStatus()});
            }
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

    private void loadSelectedOrder() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        int row = table.convertRowIndexToModel(viewRow);
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        Order o = OrderStore.findById(id);
        if (o != null) {
            addressField.setText(o.getDeliveryAddress());
            phoneField.setText(o.getDeliveryPhone());
            statusBox.setSelectedItem(o.getDeliveryStatus());
        }
    }

    private void saveDelivery() {
        Order o = selectedOrder();
        if (o == null) return;

        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        if (address.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Delivery address and phone number are required.",
                    "Incomplete information", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!phone.matches("[0-9+() -]{7,20}")) {
            JOptionPane.showMessageDialog(this, "Enter a valid phone number (7-20 characters).",
                    "Invalid phone number", JOptionPane.ERROR_MESSAGE);
            return;
        }

        o.setDeliveryAddress(address);
        o.setDeliveryPhone(phone);
        o.setDeliveryStatus((String) statusBox.getSelectedItem());
        refreshData();
        selectOrder(o.getOrderId());
        JOptionPane.showMessageDialog(this, "Delivery information saved successfully.",
                "Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void selectOrder(int id) {
        for (int row = 0; row < model.getRowCount(); row++) {
            if (Integer.parseInt(model.getValueAt(row, 0).toString()) == id) {
                int viewRow = table.convertRowIndexToView(row);
                table.setRowSelectionInterval(viewRow, viewRow);
                break;
            }
        }
    }

    private String emptyText(String value) {
        return value == null || value.trim().isEmpty() ? "Not provided" : value;
    }
}
