package com.salesystem.app;

import com.salesystem.db.Database;
import com.salesystem.employee.EmployeesPanel;
import com.salesystem.inventory.InventoryPanel;
import com.salesystem.product.ProductsPanel;
import com.salesystem.sale.CreateSalePanel;
import com.salesystem.sale.CreateSaleController;
import com.salesystem.sale.SaleHistoryPanel;
import com.salesystem.ui.MiValiaTheme;
import java.awt.*;
import javax.swing.*;

public final class MainMenuPanel extends JPanel {
    private final JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 10));
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JLabel heading = new JLabel("Bienvenido a MiValia");
    private JButton selected;

    public MainMenuPanel(Database database, ImageIcon logo) {
        super(new BorderLayout(24, 0));
        setBackground(MiValiaTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buttons.setOpaque(false);
        JPanel sidebar = new JPanel(new BorderLayout(0, 12));
        sidebar.setOpaque(false);
        JLabel brand = new JLabel(logo);
        brand.getAccessibleContext().setAccessibleName("MiValia");
        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(buttons, BorderLayout.CENTER);
        JPanel rail = new JPanel(new BorderLayout());
        rail.setOpaque(false);
        rail.add(sidebar, BorderLayout.NORTH);
        add(rail, BorderLayout.WEST);

        JPanel workspace = new JPanel(new BorderLayout(0, 14));
        workspace.setOpaque(false);
        heading.setForeground(MiValiaTheme.NAVY);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 24f));
        workspace.add(heading, BorderLayout.NORTH);
        content.setBorder(BorderFactory.createLineBorder(MiValiaTheme.BORDER));
        JPanel welcome = new JPanel();
        welcome.setBackground(Color.WHITE);
        content.add(welcome, "welcome");
        workspace.add(content, BorderLayout.CENTER);
        add(workspace, BorderLayout.CENTER);

        var sale = new CreateSalePanel();
        var checkout = new CreateSaleController(sale, database);
        var history = new SaleHistoryPanel(database);
        var inventory = new InventoryPanel(database);
        var products = new ProductsPanel(database);
        var employees = new EmployeesPanel(database);
        addScreen("Create Sale", sale, checkout::load);
        addScreen("Sales History", history, history::load);
        addScreen("Inventory", inventory, inventory::load);
        addScreen("Products", products, products::load);
        addScreen("Employees", employees, employees::load);
    }

    private void addScreen(String name, JPanel screen, Runnable onOpen) {
        content.add(screen, name);
        JButton button = new JButton(name);
        MiValiaTheme.styleButton(button, false);
        buttons.add(button);
        button.addActionListener(event -> {
            if (selected != null) MiValiaTheme.styleButton(selected, false);
            selected = button;
            MiValiaTheme.styleButton(button, true);
            heading.setText(name);
            cards.show(content, name);
            onOpen.run();
        });
    }
}

