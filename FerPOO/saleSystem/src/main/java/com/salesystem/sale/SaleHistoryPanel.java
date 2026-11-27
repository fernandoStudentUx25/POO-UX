package com.salesystem.sale;

import com.salesystem.db.Database;
import com.salesystem.ui.MiValiaTheme;
import java.awt.BorderLayout;
import java.awt.Color;
import com.salesystem.ui.DatabaseTask;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SaleHistoryPanel extends JPanel {
    private final Database database;
    private final SaleHistoryRepository repository = new SaleHistoryRepository();
    private final JTable sales = table("Sale ID", "Date", "Employee", "Total");
    private final JTable details = table("Product", "Quantity", "Unit price", "Subtotal");
    private final JLabel status = new JLabel("Sales");
    private final JLabel detailTitle = new JLabel("Select a sale to view its items.");
    private final JButton refresh = new JButton("Refresh");
    private int selectionVersion;

    public SaleHistoryPanel(Database database) {
        super(new BorderLayout(0, 8));
        this.database = database;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(status);
        toolbar.add(refresh, BorderLayout.EAST);
        MiValiaTheme.styleButton(refresh, false);
        add(toolbar, BorderLayout.NORTH);
        JPanel lower = new JPanel(new BorderLayout(0, 6));
        lower.setBackground(Color.WHITE);
        lower.add(detailTitle, BorderLayout.NORTH);
        lower.add(new JScrollPane(details));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(sales), lower);
        split.setResizeWeight(0.55);
        add(split);
        refresh.addActionListener(event -> load());
        sales.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) loadSelection();
        });
    }

    public void load() {
        if (!refresh.isEnabled()) return;
        refresh.setEnabled(false);
        sales.clearSelection();
        model(sales).setRowCount(0);
        status.setText("Loading sales...");
        DatabaseTask.run(database, repository::sales, rows -> {
            for (var row : rows) {
                model(sales).addRow(new Object[]{row.id(), row.date(), row.employee(), row.total()});
            }
            status.setText(rows.isEmpty() ? "No sales found" : "Sales: " + rows.size());
            refresh.setEnabled(true);
        }, error -> {
            status.setText("Could not load sales. Select Refresh to retry.");
            refresh.setEnabled(true);
            JOptionPane.showMessageDialog(this, error, "Sales History", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void loadSelection() {
        int version = ++selectionVersion;
        model(details).setRowCount(0);
        int index = sales.getSelectedRow();
        if (index < 0) {
            detailTitle.setText("Select a sale to view its items.");
            return;
        }
        int saleId = (Integer) sales.getValueAt(index, 0);
        detailTitle.setText("Loading items for sale #" + saleId);
        DatabaseTask.run(database, connection -> repository.lines(connection, saleId), rows -> {
            if (version != selectionVersion) return;
            for (var row : rows) {
                model(details).addRow(new Object[]{row.product(), row.quantity(), row.unitPrice(), row.subtotal()});
            }
            detailTitle.setText("Sale #" + saleId + (rows.isEmpty() ? " - no items" : " - items"));
        }, error -> {
            if (version == selectionVersion) detailTitle.setText("Could not load items for sale #" + saleId + ". Refresh to retry.");
        });
    }

    private static DefaultTableModel model(JTable table) {
        return (DefaultTableModel) table.getModel();
    }

    private static JTable table(String... columns) {
        JTable table = new JTable(new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        MiValiaTheme.styleTable(table);
        return table;
    }
}

