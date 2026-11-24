package screens;

import controller.OrderController;
import data.Store;
import models.Payment;
import ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessPaymentScreen extends JPanel {
    private final DefaultTableModel pendingModel;
    private final DefaultTableModel allModel;
    private final JTable pendingTable;
    private final JTextField remarksField = new JTextField(22);
    private final Runnable onDataChanged;
    private List<Payment> pending = new ArrayList<Payment>();

    public ProcessPaymentScreen(Runnable onDataChanged, Runnable goToAssign) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(Theme.heading("Process Payment"), BorderLayout.NORTH);

        pendingModel = new DefaultTableModel(new Object[]{"Payment ID", "Booking ID", "Payer",
                "Method", "Reference", "Amount (Rs.)", "Date"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        pendingTable = new JTable(pendingModel);
        Theme.styleTable(pendingTable);
        pendingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane pendingScroll = new JScrollPane(pendingTable);
        pendingScroll.setBorder(BorderFactory.createTitledBorder("Payments Waiting for Approval"));

        allModel = new DefaultTableModel(new Object[]{"Payment ID", "Booking ID", "Amount (Rs.)",
                "Status", "Remarks"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable allTable = new JTable(allModel);
        Theme.styleTable(allTable);
        JScrollPane allScroll = new JScrollPane(allTable);
        allScroll.setBorder(BorderFactory.createTitledBorder("All Payments"));

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 10));
        center.setOpaque(false);
        center.add(pendingScroll);
        center.add(allScroll);
        add(center, BorderLayout.CENTER);

        JButton approve = Theme.button("Approve Payment");
        JButton reject = Theme.button("Reject Payment");
        JButton next = Theme.button("Go to Assign Salesman");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(new JLabel("Remarks:"));
        actions.add(remarksField);
        actions.add(approve); actions.add(reject); actions.add(next);
        add(actions, BorderLayout.SOUTH);

        approve.addActionListener(e -> process(true));
        reject.addActionListener(e -> process(false));
        next.addActionListener(e -> goToAssign.run());
        refreshData();
    }

    public void refreshData() {
        pending = OrderController.pendingPayments();
        pendingModel.setRowCount(0);
        for (Payment p : pending) {
            pendingModel.addRow(new Object[]{p.getPaymentId(), p.getBookingId(), p.getPayerName(),
                    p.getMethod(), p.getReference(), String.format("%.2f", p.getAmount()), p.getDate()});
        }
        allModel.setRowCount(0);
        for (Payment p : Store.getPayments()) {
            allModel.addRow(new Object[]{p.getPaymentId(), p.getBookingId(),
                    String.format("%.2f", p.getAmount()), p.getStatus(), p.getRemarks()});
        }
    }

    private void process(boolean approve) {
        int viewRow = pendingTable.getSelectedRow();
        Payment p = viewRow < 0 ? null : pending.get(pendingTable.convertRowIndexToModel(viewRow));
        try {
            OrderController.processPayment(p, approve, remarksField.getText());
            remarksField.setText("");
            refreshData();
            onDataChanged.run();
            JOptionPane.showMessageDialog(this, "Payment " + p.getPaymentId() + (approve ? " approved." : " rejected."),
                    "Done", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot process", JOptionPane.WARNING_MESSAGE);
        }
    }
}
