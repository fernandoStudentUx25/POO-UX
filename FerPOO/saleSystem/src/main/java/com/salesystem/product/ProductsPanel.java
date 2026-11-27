package com.salesystem.product;

import com.salesystem.db.Database;
import com.salesystem.ui.DatabaseTask;
import com.salesystem.ui.RecordsPanel;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;

public final class ProductsPanel extends RecordsPanel<Product> {
    private final ProductRepository repository;

    public ProductsPanel(Database database) {
        this(database, new ProductRepository());
    }

    private ProductsPanel(Database database, ProductRepository repository) {
        super(database, new String[]{"ID", "Product", "Category", "Price", "Stock"}, repository::findAll,
                p -> new Object[]{p.id(), p.name(), p.category(), p.price(), p.stock()});
        this.repository = repository;
        addAction("Create product", this::chooseCategoryAndCreate);
    }

    private void chooseCategoryAndCreate() {
        busy(true);
        DatabaseTask.run(database, repository::categories, categories -> {
            busy(false);
            if (categories.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Add a category to the database before creating a product.");
                return;
            }
            createProduct(categories);
        }, this::failed);
    }

    private void createProduct(List<ProductRepository.Category> categories) {
        var nameField = new JTextField(20);
        var priceField = new JTextField();
        var stockField = new JTextField("0");
        var categoryField = new JComboBox<>(categories.toArray(ProductRepository.Category[]::new));
        Object[] fields = {"Product name", nameField, "Category", categoryField,
                "Unit price (IVA included)", priceField, "Opening stock", stockField};
        while (JOptionPane.showConfirmDialog(this, fields, "Create product",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            String name = nameField.getText().strip();
            try {
                BigDecimal price = new BigDecimal(priceField.getText().strip()).setScale(2, java.math.RoundingMode.UNNECESSARY);
                int stock = Integer.parseInt(stockField.getText().strip());
                if (name.isEmpty() || name.length() > 120 || price.signum() <= 0 || price.precision() > 12 || stock < 0) {
                    throw new IllegalArgumentException();
                }
                int categoryId = categories.get(categoryField.getSelectedIndex()).id();
                busy(true);
                DatabaseTask.run(database, c -> repository.create(c, name, categoryId, price, stock), id -> saved(), this::failed);
                return;
            } catch (IllegalArgumentException | ArithmeticException invalid) {
                JOptionPane.showMessageDialog(this, "Enter a name (up to 120 characters), a positive price with up to two decimal places, and a nonnegative whole stock quantity.");
            }
        }
    }
}

