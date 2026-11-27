package com.salesystem.sale;

import java.util.List;
import java.math.BigDecimal;
import com.salesystem.product.Product;
import com.salesystem.ui.MiValiaTheme;
import java.awt.*;

import java.text.ParseException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CreateSalePanel extends JPanel {

    private final JComboBox<Product> products = new JComboBox<>();
    private final JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    private final JButton add = new JButton("Add to cart");
    private final JButton remove = new JButton("Remove item");
    private final JButton retry = new JButton("Retry");
    private final JButton complete = new JButton("Complete Sale");
    private final JLabel status = new JLabel();
    private final JLabel totals = new JLabel();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Product", "Quantity", "Unit price", "Line total"}, 0) {
        public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    public CreateSalePanel() {
        super(new BorderLayout(0, 10));

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        add(productPicker(), BorderLayout.NORTH);
        MiValiaTheme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(checkoutControls(), BorderLayout.SOUTH);
        MiValiaTheme.styleButton(add, false);
        MiValiaTheme.styleButton(complete, true);
        retry.setVisible(false);
    }

    private JPanel productPicker() {
        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.setOpaque(false);
        top.add(products, BorderLayout.CENTER);
        products.getAccessibleContext().setAccessibleName("Product");
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        products.setRenderer((list, row, index, selected, focus) -> renderer.getListCellRendererComponent(
                list, row == null ? "Select product" : row.name() + " (#" + row.id() + ")",
                index, selected, focus));
        JPanel controls = new JPanel();
        controls.setOpaque(false);
        JLabel label = new JLabel("Quantity");
        label.setLabelFor(quantity);
        controls.add(label);
        ((JSpinner.DefaultEditor) quantity.getEditor()).getTextField().setColumns(4);
        controls.add(quantity);
        controls.add(add);
        top.add(controls, BorderLayout.EAST);
        top.add(status, BorderLayout.SOUTH);
        return top;
    }

    private JPanel checkoutControls() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.add(remove);
        actions.add(retry);
        footer.add(actions, BorderLayout.WEST);
        footer.add(totals, BorderLayout.NORTH);
        footer.add(complete, BorderLayout.EAST);
        return footer;
    }


    void bind(CreateSaleController controller) {
        add.addActionListener(event -> controller.addSelected());
        remove.addActionListener(event -> controller.removeSelected());
        retry.addActionListener(event -> controller.load());
        complete.addActionListener(event -> controller.completeSale());
        products.addActionListener(event -> {
            Product product = selectedProduct();
            add.setEnabled(products.isEnabled() && product != null && product.stock() > 0);
            if (product != null) showStatus("Available: " + product.stock() + "     Unit price: $" + product.price());
        });
        table.getSelectionModel().addListSelectionListener(event ->
                remove.setEnabled(table.isEnabled() && selectedLine() >= 0));
        add.setEnabled(false);
    }

    Product selectedProduct() {
        return (Product) products.getSelectedItem();
    }

    int selectedQuantity() throws ParseException {
        quantity.commitEdit();
        return (Integer) quantity.getValue();
    }

    int selectedLine() {
        return table.getSelectedRow();
    }

    void showProducts(List<Product> rows) {
        products.setModel(new DefaultComboBoxModel<>(rows.toArray(Product[]::new)));
        products.setSelectedIndex(-1);
    }

    void showCart(SaleCart cart) {
        model.setRowCount(0);
        for (var line : cart.lines()) {
            model.addRow(new Object[]{line.product().name(), line.quantity(), line.product().price(), line.amount()});
        }
        remove.setEnabled(false);
        complete.setEnabled(model.getRowCount() > 0);
        totals.setText("Before IVA $" + cart.subtotal() + "    Included IVA (16%) $" + cart.tax() + "    Total $" + cart.total());
    }

    void showStatus(String message) {
        status.setText(message);
    }

    void showRetry(boolean visible) {
        retry.setVisible(visible);
    }

    void setSaving(boolean saving) {
        products.setEnabled(!saving);
        quantity.setEnabled(!saving);
        table.setEnabled(!saving);
        Product product = selectedProduct();
        add.setEnabled(!saving && product != null && product.stock() > 0);
        remove.setEnabled(!saving && selectedLine() >= 0);
        complete.setEnabled(!saving && model.getRowCount() > 0);
    }

    String askEmployeeId(String previousId) {
        JTextField employee = new JTextField(previousId, 12);
        Object[] fields = {totals.getText(), "Employee ID", employee};
        int answer = JOptionPane.showConfirmDialog(this, fields, "Confirm sale",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        return answer == JOptionPane.OK_OPTION ? employee.getText().strip() : null;
    }

    void saleSaved(int id, BigDecimal total) {
        quantity.setValue(1);
        JOptionPane.showMessageDialog(this, "Sale #" + id + " saved. Total: $" + total,
                "Sale completed", JOptionPane.INFORMATION_MESSAGE);
    }

    void showCheckoutError(String message) {
        JOptionPane.showMessageDialog(this, message, "Checkout error", JOptionPane.ERROR_MESSAGE);
    }
}
