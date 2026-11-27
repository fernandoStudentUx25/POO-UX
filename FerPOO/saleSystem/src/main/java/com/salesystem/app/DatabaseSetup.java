
package com.salesystem.app;

import com.salesystem.db.Database;
import com.salesystem.db.WindowsPasswordStore;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.swing.*;

final class DatabaseSetup {

    private DatabaseSetup() {}

    static Database connect() throws Exception {

        // Linux / Docker
        if (!System.getProperty("os.name").startsWith("Windows")) {
            String password = System.getenv("SALE_DB_PASSWORD");

            if (password == null || password.isBlank()) {
                throw new IllegalStateException(
                    "SALE_DB_PASSWORD is required."
                );
            }

            return validateConnection(password);
        }

        // Windows
        var store = new WindowsPasswordStore(
            Path.of(
                System.getenv("LOCALAPPDATA"),
                "MiValia",
                "connection.dat"
            )
        );

        String password = System.getenv("SALE_DB_PASSWORD");

        if (password == null) {
            try {
                password = store.read();
            } catch (IOException ex) {
                message("The saved credential could not be read. Enter the database password again.");
            }
        }

        while (true) {
            boolean entered = password == null;

            if (entered) {
                password = onSwing(DatabaseSetup::askPassword);
            }

            if (password == null) {
                return null;
            }

            Database database;

            try {
                database = validateConnection(password);
            } catch (SQLException ex) {
                if ("28P01".equals(ex.getSQLState())) {
                    message("PostgreSQL rejected the password. Try again or cancel setup.");
                    password = null;
                    continue;
                }

                message("Could not open Sale_System. Check that PostgreSQL is running.\n"
                    + ex.getMessage());

                return null;
            }

            if (entered) {
                try {
                    store.save(password);
                } catch (IOException ex) {
                    message("Connected for this session. Windows could not save the password for the next launch.");
                }
            }

            return database;
        }
    }

    private static Database validateConnection(String password)
            throws SQLException {

        Database database = new Database(password);

        try (var connection = database.open()) {
            if (!connection.isValid(5)) {
                throw new SQLException(
                    "Database connection could not be verified."
                );
            }
        }

        return database;
    }

    private static String askPassword() {
        JPasswordField field = new JPasswordField(20);

        Object[] fields = {
            "PostgreSQL password for Sale_System",
            field
        };

        if (JOptionPane.showConfirmDialog(
                null,
                fields,
                "MiValia database setup",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        ) != JOptionPane.OK_OPTION) {
            return null;
        }

        char[] chars = field.getPassword();

        try {
            return new String(chars);
        } finally {
            Arrays.fill(chars, '\0');
            field.setText("");
        }
    }

    private static void message(String text) throws Exception {
        onSwing(() -> {
            JOptionPane.showMessageDialog(
                null,
                text,
                "MiValia database setup",
                JOptionPane.INFORMATION_MESSAGE
            );
            return null;
        });
    }

    private static <T> T onSwing(Callable<T> action)
            throws InterruptedException, ExecutionException {

        FutureTask<T> task = new FutureTask<>(action);
        SwingUtilities.invokeLater(task);
        return task.get();
    }
}
