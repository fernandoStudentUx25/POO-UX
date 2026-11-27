package com.salesystem.ui;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public final class MiValiaTheme {
    public static final Color BLUE = new Color(0x265CF0);
    public static final Color NAVY = new Color(0x14213D);
    public static final Color BACKGROUND = new Color(0xF3F7FF);
    public static final Color BORDER = new Color(0xD8E3F7);

    private MiValiaTheme() {}

    public static void styleButton(JButton button, boolean primary) {
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(primary ? BLUE : Color.WHITE);
        button.setForeground(primary ? Color.WHITE : NAVY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(primary ? BLUE : BORDER),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));
    }
    public static void styleTable(javax.swing.JTable table) {
        table.setRowHeight(26);
        table.setFillsViewportHeight(true);
        table.setForeground(NAVY);
        table.setBackground(Color.WHITE);
        table.setGridColor(BORDER);
        table.setSelectionBackground(BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(BACKGROUND);
        table.getTableHeader().setForeground(NAVY);
    }
}
