package com.note0.simple;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserDAO (Data Access Object) handles all database operations related to the User.
 * This class isolates the SQL logic from the rest of the application.
 */
public class UserDAO {

    /**
     * Inserts a new user record into the database.
     * It hashes the user's plain-text password before storing it for security.
     * @param user The User object containing the user's full name, email, and plain-text password.
     * @throws SQLException if a database access error occurs or if the email already exists.
     */
    public void registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, password_hash, role, is_active, is_verified) VALUES (?, ?, ?, ?, ?, ?)";
        
        // Hash the password using jBCrypt
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        // Use try-with-resources to ensure the connection and statement are always closed
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the values for the placeholders (?) in the SQL query
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, hashedPassword); // Store the secure hashed password
            pstmt.setString(4, "USER");         // Default role for new users
            pstmt.setBoolean(5, true);          // New users are active by default
            pstmt.setBoolean(6, false);         // New users are not verified by default

            // Execute the SQL INSERT command
            pstmt.executeUpdate();
        }
    }

    /**
     * Attempts to authenticate a user based on their email and plain-text password.
     * @param email The email provided by the user.
     * @param plainPassword The plain-text password provided by the user.
     * @return A complete User object (including id, name, and role) if authentication is successful.
     *         Returns null if the email is not found or if the password does not match.
     * @throws SQLException if a database access error occurs.
     */
    public User loginUser(String email, String plainPassword) throws SQLException {
        // SQL query to retrieve the user's details based on their email
        String sql = "SELECT id, full_name, password_hash, role FROM users WHERE email = ?";
        
        User user = null;

        // Use try-with-resources for automatic resource management
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if the database returned a row
                if (rs.next()) {
                    // A user with that email was found, now retrieve the stored password hash
                    String storedHash = rs.getString("password_hash");

                    // Use BCrypt to securely check if the provided plain password matches the stored hash
                    if (BCrypt.checkpw(plainPassword, storedHash)) {
                        // Passwords match! Populate the User object with data from the database.
                        user = new User();
                        user.setId(rs.getLong("id"));
                        user.setFullName(rs.getString("full_name"));
                        user.setEmail(email);
                        user.setRole(rs.getString("role"));
                    }
                }
            }
        }
        
        // Return the user object if login was successful, or null if it failed
        return user;
    }
}