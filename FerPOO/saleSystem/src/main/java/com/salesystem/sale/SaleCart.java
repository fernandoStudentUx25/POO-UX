package com.salesystem.sale;

import com.salesystem.product.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class SaleCart {
    static final BigDecimal IVA_RATE = new BigDecimal("0.16");
    record Line(Product product, int quantity) {
        BigDecimal amount() {
            return product.price().multiply(BigDecimal.valueOf(quantity));
        }
    }

    private final Map<Integer, Line> lines = new LinkedHashMap<>();

    void add(Product product, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Enter a positive quantity.");
        Line existing = lines.get(product.id());
        int previous = existing == null ? 0 : existing.quantity();
        if (quantity > product.stock() - previous) {
            throw new IllegalArgumentException("Available: " + product.stock() + ". Already in cart: " + previous + ".");
        }
        lines.put(product.id(), new Line(product, previous + quantity));
    }

    List<Line> lines() {
        return List.copyOf(lines.values());
    }

    void remove(int id) {
        lines.remove(id);
    }

    void clear() {
        lines.clear();
    }

    BigDecimal total() {
        return totalOf(lines());
    }

    static BigDecimal totalOf(List<Line> lines) {
        return lines.stream().map(Line::amount).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }

    BigDecimal tax() {
        return total().subtract(subtotal());
    }

    BigDecimal subtotal() {
        return total().divide(BigDecimal.ONE.add(IVA_RATE), 2, RoundingMode.HALF_UP);
    }
}



