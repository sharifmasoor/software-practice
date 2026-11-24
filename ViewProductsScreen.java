package screens;

import controller.OrderController;
import models.Product;
import ui.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ViewProductsScreen extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final JComboBox<String> categoryBox;
    private final JSpinner quantity;
    private final JLabel description;
    private final JLabel cartLabel;
    private final Runnable onDataChanged;
    private List<Product> shown = new ArrayList<Product>();

    public ViewProductsScreen(Runnable onDataChanged, Runnable goToCart) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        top.add(Theme.heading("View Products"));
        top.add(Box.createHorizontalStrut(20));
        top.add(new JLabel("Category:"));
        categoryBox = new JComboBox<String>(new String[]{"All", "Cakes", "Snacks", "Bread"});
        top.add(categoryBox);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Product", "Category", "Price (Rs.)", "In Stock"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        description = new JLabel("Select a product to see its description.");
        JPanel center = new JPanel(new BorderLayout(0, 6));
        center.setOpaque(false);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(description, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);

        quantity = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        JButton add = Theme.button("Add to Cart");
        JButton next = Theme.button("Go to Place Order");
        cartLabel = new JLabel();
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(new JLabel("Quantity:"));
        actions.add(quantity);
        actions.add(add);
        actions.add(next);
        actions.add(cartLabel);
        add(actions, BorderLayout.SOUTH);

        categoryBox.addActionListener(e -> refreshData());
        table.getSelectionModel().addListSelectionListener(e -> {
            Product p = selectedProduct(false);
            if (p != null) description.setText(p.getName() + ": " + p.getDescription());
        });
        add.addActionListener(e -> addToCart());
        next.addActionListener(e -> goToCart.run());
        refreshData();
    }

    public void refreshData() {
        shown = OrderController.productsInCategory((String) categoryBox.getSelectedItem());
        model.setRowCount(0);
        for (Product p : shown) {
            model.addRow(new Object[]{p.getName(), p.getCategory(), String.format("%.2f", p.getPrice()),
                    p.getStock() > 0 ? String.valueOf(p.getStock()) : "Out of stock"});
        }
        cartLabel.setText("Items in cart: " + OrderController.cartCount());
    }

    private Product selectedProduct(boolean warn) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            if (warn) JOptionPane.showMessageDialog(this, "Please select a product first.",
                    "No product selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return shown.get(table.convertRowIndexToModel(viewRow));
    }

    private void addToCart() {
        Product p = selectedProduct(true);
        if (p == null) return;
        int q = (Integer) quantity.getValue();
        String problem = OrderController.addToCart(p, q);
        if (problem != null) {
            JOptionPane.showMessageDialog(this, problem, "Cannot add", JOptionPane.WARNING_MESSAGE);
            return;
        }
        quantity.setValue(1);
        description.setText("Added " + q + " x " + p.getName() + " to your cart.");
        cartLabel.setText("Items in cart: " + OrderController.cartCount());
        onDataChanged.run();
    }
}
