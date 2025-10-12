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

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}