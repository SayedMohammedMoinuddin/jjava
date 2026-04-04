package com.microbench.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DatabaseManager {

    private final String dbUrl;

    public DatabaseManager() {
        // Default to ~/.microbench/benchmarks.db
        String userHome = System.getProperty("user.home");
        Path dirPath = Paths.get(userHome, ".microbench");
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (Exception e) {
                System.err.println("Failed to create directory for DB: " + e.getMessage());
            }
        }
        this.dbUrl = "jdbc:sqlite:" + dirPath.resolve("benchmarks.db").toString();
    }

    // Allows injecting a different URL (like memory DB) for testing
    public DatabaseManager(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }

    public void initializeSchema() {
        String createProgramsTable = """
            CREATE TABLE IF NOT EXISTS programs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                source TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                description TEXT
            );
        """;

        String createBenchmarksTable = """
            CREATE TABLE IF NOT EXISTS benchmark_runs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                program_id INTEGER NOT NULL REFERENCES programs(id),
                run_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                jvm_time_ns INTEGER NOT NULL,
                native_time_ns INTEGER NOT NULL,
                jvm_peak_rss_kb INTEGER,
                native_peak_rss_kb INTEGER,
                jvm_exit_code INTEGER,
                native_exit_code INTEGER,
                optimization_level TEXT NOT NULL,
                warmup_enabled BOOLEAN NOT NULL,
                warmup_iterations INTEGER
            );
        """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createProgramsTable);
            stmt.execute(createBenchmarksTable);
        } catch (SQLException e) {
            System.err.println("Error initializing database schema: " + e.getMessage());
        }
    }
}
