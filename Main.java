import screens.BookOrderScreen;
import screens.PlaceOrderScreen;
import screens.ViewProductsScreen;
import ui.Theme;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private final JTabbedPane tabs = new JTabbedPane();
    private final ViewProductsScreen productsScreen;
    private final PlaceOrderScreen placeScreen;
    private final BookOrderScreen bookScreen;

    public Main() {
        super("Online Bakery Ordering System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 680);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        productsScreen = new ViewProductsScreen(this::refreshScreens, () -> tabs.setSelectedIndex(1));
        placeScreen = new PlaceOrderScreen(this::refreshScreens, () -> tabs.setSelectedIndex(2));
        bookScreen = new BookOrderScreen(this::refreshScreens);

        tabs.setBackground(Theme.BACKGROUND);
        tabs.addTab("View Products", productsScreen);
        tabs.addTab("Place Order", placeScreen);
        tabs.addTab("Book Order", bookScreen);
        // reload the data whenever another tab is opened
        tabs.addChangeListener(e -> refreshScreens());

        add(tabs, BorderLayout.CENTER);
    }

    private void refreshScreens() {
        productsScreen.refreshData();
        placeScreen.refreshData();
        bookScreen.refreshData();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
