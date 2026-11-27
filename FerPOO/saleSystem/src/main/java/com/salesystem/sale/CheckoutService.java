package com.salesystem.sale;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

final class CheckoutService {
    int complete(Connection connection, int employeeId, List<SaleCart.Line> lines)
            throws SQLException {
        if (lines.isEmpty()) throw new IllegalArgumentException("Add at least one product.");
        BigDecimal total = SaleCart.totalOf(lines);
        connection.setAutoCommit(false);
        try {
            setConstraints(connection, true);
            requireEmployee(connection, employeeId);
            int saleId = insertSale(connection, employeeId, total);
            var remainingStock = insertLines(connection, saleId, lines);
            updateReceiptTotal(connection, saleId, total);
            // The database validates the complete receipt and may deduct stock here.
            setConstraints(connection, false);
            applyRemainingStock(connection, remainingStock);
            connection.commit();
            return saleId;
        } catch (SQLException | RuntimeException failure) {
            try {
                connection.rollback();
            } catch (SQLException rollbackFailure) {
                failure.addSuppressed(rollbackFailure);
            }
            throw failure;
        }
    }

    private void setConstraints(Connection connection, boolean deferred) throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute(deferred ? "SET CONSTRAINTS ALL DEFERRED" : "SET CONSTRAINTS ALL IMMEDIATE");
        }
    }

    private void requireEmployee(Connection connection, int employeeId) throws SQLException {
        try (var employee = connection.prepareStatement(
                "SELECT 1 FROM public.employee WHERE employee_id = ? FOR KEY SHARE")) {
            employee.setInt(1, employeeId);
            try (var rows = employee.executeQuery()) {
                if (!rows.next()) throw new SQLException("Employee ID " + employeeId + " does not exist.");
            }
        }
    }

    private int insertSale(Connection connection, int employeeId, BigDecimal total) throws SQLException {
        try (var insert = connection.prepareStatement("""
                INSERT INTO public.sale (employee_id, date, total)
                VALUES (?, CURRENT_TIMESTAMP, ?) RETURNING sale_id
                """)) {
            insert.setInt(1, employeeId);
            insert.setBigDecimal(2, total);
            try (var rows = insert.executeQuery()) {
                rows.next();
                return rows.getInt(1);
            }
        }
    }

    private Map<Integer, Integer> insertLines(Connection connection, int saleId, List<SaleCart.Line> lines)
            throws SQLException {
        boolean generatedSubtotal;
        try (var columns = connection.getMetaData().getColumns(null, "public", "sale_detail", "subtotal")) {
            generatedSubtotal = columns.next() && "YES".equals(columns.getString("IS_GENERATEDCOLUMN"));
        }
        String insertLine = generatedSubtotal
                ? "INSERT INTO public.sale_detail (sale_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)"
                : "INSERT INTO public.sale_detail (sale_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";

        var remainingStock = new LinkedHashMap<Integer, Integer>();
        try (var lock = connection.prepareStatement(
                     "SELECT stock, price FROM public.product WHERE product_id = ? FOR UPDATE");
             var detail = connection.prepareStatement(insertLine)) {
            // All checkouts acquire product locks in the same order.
            for (var line : lines.stream().sorted(Comparator.comparingInt(l -> l.product().id())).toList()) {
                var product = line.product();
                lock.setInt(1, product.id());
                int remaining;
                try (var row = lock.executeQuery()) {
                    if (!row.next() || row.getInt("stock") < line.quantity()) {
                        throw new SQLException("Insufficient stock for " + product.name() + ".");
                    }
                    if (product.price().compareTo(row.getBigDecimal("price")) != 0) {
                        throw new SQLException("Price changed for " + product.name()
                                + ". Remove the item and add it again at the current price.");
                    }
                    remaining = row.getInt("stock") - line.quantity();
                }
                detail.setInt(1, saleId);
                detail.setInt(2, product.id());
                detail.setInt(3, line.quantity());
                detail.setBigDecimal(4, product.price());
                if (!generatedSubtotal) detail.setBigDecimal(5, line.amount());
                detail.executeUpdate();

                remainingStock.put(product.id(), remaining);
            }
        }
        return remainingStock;
    }

    private void updateReceiptTotal(Connection connection, int saleId, BigDecimal total) throws SQLException {
        try (var receipt = connection.prepareStatement(
                "UPDATE public.sale SET total = ? WHERE sale_id = ? AND total IS DISTINCT FROM ?")) {
            receipt.setBigDecimal(1, total);
            receipt.setInt(2, saleId);
            receipt.setBigDecimal(3, total);
            receipt.executeUpdate();
        }
    }

    private void applyRemainingStock(Connection connection, Map<Integer, Integer> remainingStock) throws SQLException {
        try (var stock = connection.prepareStatement(
                "UPDATE public.product SET stock = ? WHERE product_id = ? AND stock IS DISTINCT FROM ?")) {
            for (var entry : remainingStock.entrySet()) {
                stock.setInt(1, entry.getValue());
                stock.setInt(2, entry.getKey());
                stock.setInt(3, entry.getValue());
                stock.executeUpdate();
            }
        }
    }
}
