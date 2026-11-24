import screens.ManageDeliveryScreen;
import screens.ManageOrdersScreen;
import screens.ManageSalesScreen;
import ui.Theme;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private final ManageOrdersScreen ordersScreen;
    private final ManageSalesScreen salesScreen;
    private final ManageDeliveryScreen deliveryScreen;

    public Main() {
        super("Bakery Ordering System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);

        ordersScreen = new ManageOrdersScreen(this::refreshScreens);
        salesScreen = new ManageSalesScreen();
        deliveryScreen = new ManageDeliveryScreen();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Theme.BACKGROUND);
        tabs.addTab("Manage Orders", ordersScreen);
        tabs.addTab("Manage Sales", salesScreen);
        tabs.addTab("Manage Delivery", deliveryScreen);
        // reload the data whenever another tab is opened
        tabs.addChangeListener(e -> refreshScreens());

        add(tabs, BorderLayout.CENTER);
    }

    private void refreshScreens() {
        ordersScreen.refreshData();
        salesScreen.refreshData();
        deliveryScreen.refreshData();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
