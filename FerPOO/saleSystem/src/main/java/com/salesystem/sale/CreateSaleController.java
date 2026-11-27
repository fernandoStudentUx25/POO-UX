package com.salesystem.sale;

import com.salesystem.db.Database;
import com.salesystem.product.Product;
import com.salesystem.product.ProductRepository;
import com.salesystem.ui.DatabaseTask;
import java.text.ParseException;
import java.util.List;

public final class CreateSaleController {
    private final CreateSalePanel view;
    private final Database database;
    private final SaleCart cart = new SaleCart();
    private final ProductRepository products = new ProductRepository();
    private final CheckoutService checkout = new CheckoutService();
    private boolean loading;
    private boolean saving;
    private String previousEmployeeId = "";

    public CreateSaleController(CreateSalePanel view, Database database) {
        this.view = view;
        this.database = database;
        view.bind(this);
        view.showCart(cart);
    }

    public void load() {
        if (loading || saving) return;
        loading = true;
        view.showRetry(false);
        view.showStatus("Loading products...");
        DatabaseTask.run(database, products::findAll, rows -> {
            loading = false;
            if (saving) return;
            view.showProducts(rows);
            view.showStatus(rows.isEmpty() ? "No products available." : "Select a product.");
            view.showRetry(rows.isEmpty());
        }, error -> {
            loading = false;
            if (saving) return;
            view.showRetry(true);
            view.showStatus("Could not load products: " + error);
        });
    }

    void addSelected() {
        Product product = view.selectedProduct();
        if (saving || product == null) return;
        try {
            cart.add(product, view.selectedQuantity());
            view.showCart(cart);
            view.showStatus("Added " + product.name());
        } catch (ParseException ex) {
            view.showStatus("Enter a whole number for quantity.");
        } catch (IllegalArgumentException ex) {
            view.showStatus(ex.getMessage());
        }
    }

    void removeSelected() {
        int index = view.selectedLine();
        if (saving || index < 0) return;
        cart.remove(cart.lines().get(index).product().id());
        view.showCart(cart);
    }

    void completeSale() {
        if (saving || cart.lines().isEmpty()) return;
        if (loading) {
            view.showStatus("Please wait for products to finish loading.");
            return;
        }
        String input = view.askEmployeeId(previousEmployeeId);
        if (input == null) return;

        int employeeId;
        try {
            employeeId = Integer.parseInt(input);
            if (employeeId < 1) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            view.showStatus("Enter a valid employee ID to complete the sale.");
            return;
        }
        save(employeeId);
    }

    private void save(int employeeId) {
        var lines = cart.lines();
        var total = cart.total();
        saving = true;
        view.setSaving(true);
        view.showStatus("Saving sale...");
        DatabaseTask.run(database, connection -> checkout.complete(connection, employeeId, lines), saleId -> {
            previousEmployeeId = Integer.toString(employeeId);
            cart.clear();
            view.saleSaved(saleId, total);
            finishCheckout();
        }, error -> {
            view.showCheckoutError(error);
            finishCheckout();
        });
    }

    private void finishCheckout() {
        saving = false;
        view.showProducts(List.of());
        view.setSaving(false);
        view.showCart(cart);
        load();
    }
}
