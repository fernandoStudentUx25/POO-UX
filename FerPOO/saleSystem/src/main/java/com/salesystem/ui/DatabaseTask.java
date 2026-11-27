package com.salesystem.ui;

import com.salesystem.db.Database;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/** Runs JDBC work off the event thread and delivers its result back to Swing. */
public final class DatabaseTask {
    private DatabaseTask() {}

    @FunctionalInterface
    public interface Operation<T> {
        T execute(Connection connection) throws SQLException;
    }

    public static <T> void run(Database database, Operation<T> operation,
                               Consumer<T> success, Consumer<String> failure) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws SQLException {
                try (Connection connection = database.open()) {
                    return operation.execute(connection);
                }
            }

            @Override
            protected void done() {
                try {
                    success.accept(get());
                } catch (ExecutionException ex) {
                    failure.accept(ex.getCause().getMessage());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    failure.accept("Operation interrupted.");
                }
            }
        }.execute();
    }
}
