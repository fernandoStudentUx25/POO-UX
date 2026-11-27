package com.salesystem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private final String password;

    public Database(String password) {
        this.password = password;
    }

    public Connection open() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://[::1]:5432/Sale_System?connectTimeout=5",
                "postgres", password);
    }
}
