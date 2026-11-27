package com.salesystem.sale;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class SaleHistoryRepository {
    record Sale(int id, LocalDateTime date, String employee, BigDecimal total) {}
    record Line(String product, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {}

    List<Sale> sales(Connection connection) throws SQLException {
        String sql = """
                SELECT s.sale_id, s.date, concat_ws(' ', e.first_name, e.last_name), s.total
                FROM public.sale s LEFT JOIN public.employee e ON e.employee_id = s.employee_id
                ORDER BY s.date DESC NULLS LAST, s.sale_id DESC
                """;
        List<Sale> sales = new ArrayList<>();
        try (var statement = connection.prepareStatement(sql); var rows = statement.executeQuery()) {
            while (rows.next()) {
                sales.add(new Sale(rows.getInt(1), rows.getObject(2, LocalDateTime.class),
                        rows.getString(3), rows.getBigDecimal(4)));
            }
        }
        return sales;
    }

    List<Line> lines(Connection connection, int saleId) throws SQLException {
        String sql = """
                SELECT p.name, d.quantity, d.unit_price, d.subtotal
                FROM public.sale_detail d LEFT JOIN public.product p ON p.product_id = d.product_id
                WHERE d.sale_id = ? ORDER BY d.sale_detail_id
                """;
        List<Line> lines = new ArrayList<>();
        try (var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, saleId);
            try (var rows = statement.executeQuery()) {
                while (rows.next()) {
                    lines.add(new Line(rows.getString(1), rows.getInt(2),
                            rows.getBigDecimal(3), rows.getBigDecimal(4)));
                }
            }
        }
        return lines;
    }
}
