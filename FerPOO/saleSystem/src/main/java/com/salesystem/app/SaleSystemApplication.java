package com.salesystem.app;

import com.salesystem.db.Database;
import java.awt.Image;
import javax.swing.*;

public final class SaleSystemApplication {
    public static void main(String[] args) {
        try {
            Database database = DatabaseSetup.connect();
            if (database != null) SwingUtilities.invokeLater(() -> showMainWindow(database));
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "MiValia startup error", JOptionPane.ERROR_MESSAGE));
        }
    }

    private static void showMainWindow(Database database) {
        var logo = new ImageIcon(SaleSystemApplication.class.getResource("/brand/mivalia.png"));
        var sidebarLogo = new ImageIcon(logo.getImage().getScaledInstance(52, 52, Image.SCALE_SMOOTH));
        JFrame window = new JFrame("MiValia | Sale System");
        window.setIconImage(logo.getImage());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new MainMenuPanel(database, sidebarLogo));
        window.setSize(900, 600);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
