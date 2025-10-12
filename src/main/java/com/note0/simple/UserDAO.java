package com.note0.simple;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public void registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, password_hash, role, is_active, is_verified, college_name, semester) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, hashedPassword);
            pstmt.setString(4, "USER");
            pstmt.setBoolean(5, true);
            pstmt.setBoolean(6, false);
            pstmt.setString(7, ""); // Default college name
            pstmt.setInt(8, 1);    // Default semester

            pstmt.executeUpdate();
        }
    }

    public User loginUser(String email, String plainPassword) throws SQLException {
        String sql = "SELECT id, full_name, password_hash, role, college_name, semester FROM users WHERE email = ?";
        User user = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (BCrypt.checkpw(plainPassword, storedHash)) {
                        user = new User();
                        user.setId(rs.getLong("id"));
                        user.setFullName(rs.getString("full_name"));
                        user.setEmail(email);
                        user.setRole(rs.getString("role"));
                        user.setCollegeName(rs.getString("college_name"));
                        user.setSemester(rs.getInt("semester"));
                    }
                }
            }
        }
        return user;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, email = ?, college_name = ?, semester = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getCollegeName());
            pstmt.setInt(4, user.getSemester());
            pstmt.setLong(5, user.getId());
            pstmt.executeUpdate();
        }
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT id, full_name, role, semester FROM users";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setSemester(rs.getInt("semester"));
                users.add(user);
            }
        }
        return users;
    }

    public void deleteUser(long userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.executeUpdate();
        }
    }
}
