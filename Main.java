import screens.AssignSalesmanScreen;
import screens.BookOrderScreen;
import screens.MakePaymentScreen;
import screens.PlaceOrderScreen;
import screens.ProcessPaymentScreen;
import screens.ViewProductsScreen;
import ui.Theme;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private final JTabbedPane tabs = new JTabbedPane();
    private final ViewProductsScreen productsScreen;
    private final PlaceOrderScreen placeScreen;
    private final BookOrderScreen bookScreen;
    private final MakePaymentScreen paymentScreen;
    private final ProcessPaymentScreen processScreen;
    private final AssignSalesmanScreen assignScreen;

    public Main() {
        super("Online Bakery Ordering System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        productsScreen = new ViewProductsScreen(this::refreshScreens, () -> tabs.setSelectedIndex(1));
        placeScreen = new PlaceOrderScreen(this::refreshScreens, () -> tabs.setSelectedIndex(2));
        bookScreen = new BookOrderScreen(this::refreshScreens, () -> tabs.setSelectedIndex(3));
        paymentScreen = new MakePaymentScreen(this::refreshScreens, () -> tabs.setSelectedIndex(4));
        processScreen = new ProcessPaymentScreen(this::refreshScreens, () -> tabs.setSelectedIndex(5));
        assignScreen = new AssignSalesmanScreen(this::refreshScreens);

        tabs.setBackground(Theme.BACKGROUND);
        tabs.addTab("View Products", productsScreen);
        tabs.addTab("Place Order", placeScreen);
        tabs.addTab("Book Order", bookScreen);
        tabs.addTab("Make Payment", paymentScreen);
        tabs.addTab("Process Payment", processScreen);
        tabs.addTab("Assign Salesman", assignScreen);
        // reload the data whenever another tab is opened
        tabs.addChangeListener(e -> refreshScreens());

        add(tabs, BorderLayout.CENTER);
    }

    private void refreshScreens() {
        productsScreen.refreshData();
        placeScreen.refreshData();
        bookScreen.refreshData();
        paymentScreen.refreshData();
        processScreen.refreshData();
        assignScreen.refreshData();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
