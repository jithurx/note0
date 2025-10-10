package com.note0.simple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // --- IMPORTANT: REPLACE THESE WITH YOUR AIVEN CREDENTIALS ---
    private static final String DB_URL = "jdbc:postgresql://pg-1f5358eb-note0.k.aivencloud.com:17737/defaultdb?sslmode=require";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_zjes6XbHRJo9YtEPMuI";
    // -----------------------------------------------------------

    private static DatabaseManager instance;

    private DatabaseManager() {
        try {
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    private void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection()) {
            // Add like_count column to materials table if it doesn't exist
            conn.createStatement().execute(
                "ALTER TABLE materials ADD COLUMN IF NOT EXISTS like_count INTEGER DEFAULT 0"
            );

            // Create likes table if it doesn't exist
            conn.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS likes (" +
                "material_id BIGINT," +
                "user_id BIGINT," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (material_id, user_id)," +
                "FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")"
            );
        }
    }
}