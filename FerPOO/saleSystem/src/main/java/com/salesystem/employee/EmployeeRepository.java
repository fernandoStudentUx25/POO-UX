package com.salesystem.employee;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

final class EmployeeRepository {
    record Employee(int id, String firstName, String lastName, String phone) {}

    List<Employee> findAll(Connection connection) throws SQLException {
        var employees = new ArrayList<Employee>();
        String sql = """
                SELECT employee_id, first_name, last_name, phone FROM public.employee
                ORDER BY last_name, first_name, employee_id
                """;
        try (var statement = connection.prepareStatement(sql); var rows = statement.executeQuery()) {
            while (rows.next()) {
                employees.add(new Employee(rows.getInt("employee_id"), rows.getString("first_name"),
                        rows.getString("last_name"), rows.getString("phone")));
            }
        }
        return employees;
    }

    int create(Connection connection, String first, String last, String phone) throws SQLException {
        String sql = "INSERT INTO public.employee(first_name, last_name, phone) VALUES (?, ?, ?)";
        try (var insert = connection.prepareStatement(sql, new String[]{"employee_id"})) {
            insert.setString(1, first);
            insert.setString(2, last);
            insert.setString(3, phone);
            insert.executeUpdate();
            try (var result = insert.getGeneratedKeys()) {
                result.next();
                return result.getInt(1);
            }
        }
    }
}

