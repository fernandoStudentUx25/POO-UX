package com.salesystem.product;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {

    public List<Product> findAll(Connection connection) throws SQLException {
        String sql = """
                SELECT p.product_id, p.name, c.name AS category, p.price, p.stock
                FROM public.product p
                LEFT JOIN public.category c ON c.category_id = p.category_id
                ORDER BY p.name, p.product_id
                """;
        List<Product> products = new ArrayList<>();
        try (var statement = connection.prepareStatement(sql);
             var rows = statement.executeQuery()) {
            while (rows.next()) {
                products.add(new Product(rows.getInt("product_id"), rows.getString("name"),
                        rows.getString("category"), rows.getBigDecimal("price"), rows.getInt("stock")));
            }
        }
        return products;
    }
    record Category(int id, String name) {
        @Override public String toString() { return name; }
    }

    List<Category> categories(Connection connection) throws SQLException {
        var categories = new ArrayList<Category>();
        String sql = "SELECT category_id, name FROM public.category ORDER BY name, category_id";
        try (var statement = connection.prepareStatement(sql); var rows = statement.executeQuery()) {
            while (rows.next()) categories.add(new Category(rows.getInt(1), rows.getString(2)));
        }
        return categories;
    }

    int create(Connection connection, String name, int categoryId, BigDecimal price, int stock) throws SQLException {
        String sql = "INSERT INTO public.product(name, category_id, price, stock) VALUES (?, ?, ?, ?)";
        try (var insert = connection.prepareStatement(sql, new String[]{"product_id"})) {
            insert.setString(1, name);
            insert.setInt(2, categoryId);
            insert.setBigDecimal(3, price);
            insert.setInt(4, stock);
            insert.executeUpdate();
            try (var result = insert.getGeneratedKeys()) {
                result.next();
                return result.getInt(1);
            }
        }
    }
}


