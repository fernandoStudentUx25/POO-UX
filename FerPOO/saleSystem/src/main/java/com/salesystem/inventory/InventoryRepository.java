package com.salesystem.inventory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class InventoryRepository {
    record StockItem(int id, String name, String category, int available) {}

    List<StockItem> findAll(Connection connection) throws SQLException {
        String sql = """
                SELECT p.product_id, p.name, c.name AS category, p.stock
                FROM public.product p LEFT JOIN public.category c ON c.category_id = p.category_id
                ORDER BY p.stock, p.name, p.product_id
                """;
        var items = new ArrayList<StockItem>();
        try (var statement = connection.prepareStatement(sql); var rows = statement.executeQuery()) {
            while (rows.next()) {
                items.add(new StockItem(rows.getInt("product_id"), rows.getString("name"),
                        rows.getString("category"), rows.getInt("stock")));
            }
        }
        return items;
    }
}
