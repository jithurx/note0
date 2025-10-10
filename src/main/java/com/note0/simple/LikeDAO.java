package com.note0.simple;

import java.sql.*;

/**
 * Data Access Object for handling likes in the application.
 * This class manages all interactions with the likes table in the database.
 */
public class LikeDAO {
    private final DatabaseManager dbManager;

    public LikeDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Toggles a like for a material by a user.
     * If the like exists, it's removed. If it doesn't exist, it's added.
     * 
     * @param materialId The ID of the material to toggle the like for
     * @param userId The ID of the user toggling the like
     * @return boolean True if the material is now liked, false if it's now unliked
     * @throws SQLException if a database error occurs
     */
    public boolean toggleLike(long materialId, long userId) throws SQLException {
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;
        boolean isLiked = false;

        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // Check if like exists
            checkStmt = conn.prepareStatement(
                "SELECT 1 FROM likes WHERE material_id = ? AND user_id = ?"
            );
            checkStmt.setLong(1, materialId);
            checkStmt.setLong(2, userId);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Like exists, remove it
                deleteStmt = conn.prepareStatement(
                    "DELETE FROM likes WHERE material_id = ? AND user_id = ?"
                );
                deleteStmt.setLong(1, materialId);
                deleteStmt.setLong(2, userId);
                deleteStmt.executeUpdate();

                // Decrement like count
                updateStmt = conn.prepareStatement(
                    "UPDATE materials SET like_count = like_count - 1 WHERE id = ?"
                );
                updateStmt.setLong(1, materialId);
                updateStmt.executeUpdate();

                isLiked = false;
            } else {
                // Like doesn't exist, add it
                insertStmt = conn.prepareStatement(
                    "INSERT INTO likes (material_id, user_id) VALUES (?, ?)"
                );
                insertStmt.setLong(1, materialId);
                insertStmt.setLong(2, userId);
                insertStmt.executeUpdate();

                // Increment like count
                updateStmt = conn.prepareStatement(
                    "UPDATE materials SET like_count = like_count + 1 WHERE id = ?"
                );
                updateStmt.setLong(1, materialId);
                updateStmt.executeUpdate();

                isLiked = true;
            }

            conn.commit(); // Commit transaction
            return isLiked;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            // Close all resources
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (checkStmt != null) try { checkStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (deleteStmt != null) try { deleteStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (insertStmt != null) try { insertStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (updateStmt != null) try { updateStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets the number of likes for a material.
     * 
     * @param materialId The ID of the material
     * @return int The number of likes
     * @throws SQLException if a database error occurs
     */
    public int getLikeCount(long materialId) throws SQLException {
        String sql = "SELECT like_count FROM materials WHERE id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, materialId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("like_count");
            }
            return 0;
        }
    }

    /**
     * Checks if a user has liked a specific material.
     * 
     * @param materialId The ID of the material
     * @param userId The ID of the user
     * @return boolean True if the user has liked the material
     * @throws SQLException if a database error occurs
     */
    public boolean hasUserLiked(long materialId, long userId) throws SQLException {
        String sql = "SELECT 1 FROM likes WHERE material_id = ? AND user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, materialId);
            stmt.setLong(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
}