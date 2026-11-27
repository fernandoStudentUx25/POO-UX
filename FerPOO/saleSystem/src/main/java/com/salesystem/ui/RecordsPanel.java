package com.salesystem.ui;

import com.salesystem.db.Database;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Shared toolbar, read-only table and refresh behavior for the three record lists. */
public class RecordsPanel<T> extends JPanel {
    protected final Database database;
    private final DatabaseTask.Operation<List<T>> read;
    private final Function<T, Object[]> columns;
    private final DefaultTableModel model;
    private final JLabel status = new JLabel();
    private final JPanel actions = new JPanel();
    private final JButton refresh = new JButton("Refresh");

    protected RecordsPanel(Database database, String[] headings, DatabaseTask.Operation<List<T>> read,
                           Function<T, Object[]> columns) {
        super(new BorderLayout(0, 8));
        this.database = database;
        this.read = read;
        this.columns = columns;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        model = new DefaultTableModel(headings, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
            @Override public Class<?> getColumnClass(int column) {
                for (int row = 0; row < getRowCount(); row++) {
                    Object value = getValueAt(row, column);
                    if (value != null) return value.getClass();
                }
                return Object.class;
            }
        };
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        MiValiaTheme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        actions.setOpaque(false);
        toolbar.add(actions, BorderLayout.WEST);
        toolbar.add(status, BorderLayout.CENTER);
        toolbar.add(refresh, BorderLayout.EAST);
        MiValiaTheme.styleButton(refresh, false);
        refresh.addActionListener(event -> load());
        add(toolbar, BorderLayout.NORTH);
    }

    protected void addAction(String label, Runnable action) {
        JButton button = new JButton(label);
        MiValiaTheme.styleButton(button, true);
        button.addActionListener(event -> action.run());
        actions.add(button);
    }

    protected void busy(boolean busy) {
        refresh.setEnabled(!busy);
        for (var button : actions.getComponents()) button.setEnabled(!busy);
    }

    protected void failed(String message) {
        busy(false);
        status.setText("Operation failed.");
        JOptionPane.showMessageDialog(this, message, "Database error", JOptionPane.ERROR_MESSAGE);
    }

    public void load() {
        if (!refresh.isEnabled()) return;
        busy(true);
        status.setText("Loading...");
        DatabaseTask.run(database, read, rows -> {
            model.setRowCount(0);
            for (T row : rows) model.addRow(columns.apply(row));
            status.setText("Records: " + rows.size());
            busy(false);
        }, this::failed);
    }

    protected void saved() {
        busy(false);
        load();
    }
}
