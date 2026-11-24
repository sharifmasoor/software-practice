package ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;

/** Shared colours and small helpers so all screens look the same. */
public final class Theme {
    public static final Color BACKGROUND = new Color(240, 240, 240); // light grey
    public static final Color WHITE = Color.WHITE;
    public static final Color TEXT = Color.BLACK;
    public static final Color ACCENT = new Color(200, 218, 240);     // only accent colour (selection)

    private Theme() { }

    public static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        label.setForeground(TEXT);
        return label;
    }

    public static JButton button(String text) {
        return new JButton(text);
    }

    public static void styleTable(JTable table) {
        table.setBackground(WHITE);
        table.setForeground(TEXT);
        table.setRowHeight(24);
        table.setGridColor(new Color(200, 200, 200));
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
    }
}
