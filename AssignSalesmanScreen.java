package screens;

import controller.OrderController;
import data.Store;
import models.Booking;
import models.Salesman;
import ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AssignSalesmanScreen extends JPanel {
    private final DefaultTableModel bookingModel;
    private final DefaultTableModel salesmanModel;
    private final JTable bookingTable;
    private final JComboBox<Salesman> salesmanBox;
    private final Runnable onDataChanged;
    private List<Booking> shown = new ArrayList<Booking>();

    public AssignSalesmanScreen(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(Theme.heading("Assign Salesman"), BorderLayout.NORTH);

        bookingModel = new DefaultTableModel(new Object[]{"Booking ID", "Customer", "Address",
                "Delivery Date", "Time", "Salesman"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        bookingTable = new JTable(bookingModel);
        Theme.styleTable(bookingTable);
        bookingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane bookingScroll = new JScrollPane(bookingTable);
        bookingScroll.setBorder(BorderFactory.createTitledBorder("Paid Bookings"));

        salesmanModel = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Area",
                "Active Deliveries"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable salesmanTable = new JTable(salesmanModel);
        Theme.styleTable(salesmanTable);
        JScrollPane salesmanScroll = new JScrollPane(salesmanTable);
        salesmanScroll.setBorder(BorderFactory.createTitledBorder("Salesmen"));

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 10));
        center.setOpaque(false);
        center.add(bookingScroll);
        center.add(salesmanScroll);
        add(center, BorderLayout.CENTER);

        salesmanBox = new JComboBox<Salesman>(Store.getSalesmen().toArray(new Salesman[0]));
        JButton assign = Theme.button("Assign Salesman");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(new JLabel("Salesman:"));
        actions.add(salesmanBox);
        actions.add(assign);
        add(actions, BorderLayout.SOUTH);

        assign.addActionListener(e -> assignSalesman());
        refreshData();
    }

    public void refreshData() {
        shown = OrderController.paidBookings();
        bookingModel.setRowCount(0);
        for (Booking b : shown) {
            bookingModel.addRow(new Object[]{b.getBookingId(), b.getCustomerName(), b.getAddress(),
                    b.getDate(), b.getTime(), b.getSalesman()});
        }
        salesmanModel.setRowCount(0);
        for (Salesman s : Store.getSalesmen()) {
            salesmanModel.addRow(new Object[]{s.getId(), s.getName(), s.getPhone(), s.getArea(),
                    OrderController.activeDeliveries(s)});
        }
    }

    private void assignSalesman() {
        int viewRow = bookingTable.getSelectedRow();
        Booking b = viewRow < 0 ? null : shown.get(bookingTable.convertRowIndexToModel(viewRow));
        Salesman s = (Salesman) salesmanBox.getSelectedItem();
        try {
            OrderController.assignSalesman(b, s);
            refreshData();
            onDataChanged.run();
            JOptionPane.showMessageDialog(this, s.getName() + " is assigned to booking " + b.getBookingId() + ".",
                    "Salesman assigned", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot assign", JOptionPane.WARNING_MESSAGE);
        }
    }
}
