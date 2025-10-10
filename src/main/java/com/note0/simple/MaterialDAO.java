package com.note0.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MaterialDAO handles all database operations related to Materials.
 */
public class MaterialDAO {

    private final long currentUserId;
    private final DatabaseManager dbManager;

    public MaterialDAO(long currentUserId) {
        this.currentUserId = currentUserId;
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Retrieves materials from the database, allowing for dynamic filtering.
     * @param titleFilter A string to search for in the material's title.
     * @param branchFilter The branch to filter by.
     * @param semesterFilter The semester to filter by.
     * @param subjectFilter The subject to filter by.
     * @return A list of matching Material objects.
     * @throws SQLException if a database error occurs.
     */
    public List<Material> getMaterials(String titleFilter, String branchFilter, int semesterFilter, String subjectFilter) throws SQLException {
        // Base query with all necessary joins
        StringBuilder sql = new StringBuilder(
            "SELECT m.id, m.title, m.file_path, m.average_rating, m.like_count, u.full_name, s.name AS subject_name, " +
            "(SELECT 1 FROM likes l WHERE l.material_id = m.id AND l.user_id = ?) AS liked_by_user " +
            "FROM materials m " +
            "JOIN users u ON m.uploader_id = u.id " +
            "JOIN subjects s ON m.subject_id = s.id"
        );
        
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        // --- Dynamically build the WHERE clause based on filters ---

        if (titleFilter != null && !titleFilter.isBlank()) {
            sql.append(" WHERE LOWER(m.title) LIKE ?");
            params.add("%" + titleFilter.toLowerCase() + "%");
            hasWhere = true;
        }

        if (branchFilter != null && !branchFilter.isBlank() && !branchFilter.equals("All Branches")) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" s.branch = ?");
            params.add(branchFilter);
            hasWhere = true;
        }

        if (semesterFilter != 0) { // 0 is our value for "All Semesters"
            sql.append(hasWhere ? " AND" : " WHERE").append(" s.semester = ?");
            params.add(semesterFilter);
            hasWhere = true;
        }
        
        if (subjectFilter != null && !subjectFilter.isBlank() && !subjectFilter.equals("All Subjects")) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" s.name = ?");
            params.add(subjectFilter);
        }

        sql.append(" ORDER BY m.id DESC");
        
        List<Material> materials = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            // Set the currentUserId first for the liked_by_user subquery
            pstmt.setLong(1, currentUserId);
            
            // Start parameters array indexing from 2 since we used index 1 for currentUserId
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 2, params.get(i));
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setId(rs.getLong("id"));
                    material.setTitle(rs.getString("title"));
                    material.setFilePath(rs.getString("file_path"));
                    material.setUploaderName(rs.getString("full_name"));
                    material.setSubjectName(rs.getString("subject_name"));
                    material.setAverageRating(rs.getDouble("average_rating"));
                    material.setLikeCount(rs.getInt("like_count"));
                    material.setLikedByUser(rs.getObject("liked_by_user") != null);
                    materials.add(material);
                }
            }
        }
        return materials;
    }

    /**
     * Saves a new material record to the database.
     */
    public void addMaterial(String title, String filePath, long subjectId, long uploaderId) throws SQLException {
        String sql = "INSERT INTO materials (title, file_path, subject_id, uploader_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, filePath);
            pstmt.setLong(3, subjectId);
            pstmt.setLong(4, uploaderId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a single Material by its ID.
     */
    public Material getMaterialById(long id) throws SQLException {
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, m.like_count, u.full_name, s.name AS subject_name, " +
                     "(SELECT 1 FROM likes l WHERE l.material_id = m.id AND l.user_id = ?) AS liked_by_user " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "WHERE m.id = ?";
        Material material = null;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    material = new Material();
                    material.setId(rs.getLong("id"));
                    material.setTitle(rs.getString("title"));
                    material.setFilePath(rs.getString("file_path"));
                    material.setUploaderName(rs.getString("full_name"));
                    material.setSubjectName(rs.getString("subject_name"));
                    material.setAverageRating(rs.getDouble("average_rating"));
                    material.setLikeCount(rs.getInt("like_count"));
                    material.setLikedByUser(rs.getObject("liked_by_user") != null);
                }
            }
        }
        return material;
    }

    /**
     * Adds or updates a user's rating for a material and recalculates the average rating.
     */
    public void rateMaterial(long materialId, long userId, int score) throws SQLException {
        String upsertSql = "INSERT INTO ratings (material_id, user_id, score) VALUES (?, ?, ?) " +
                           "ON CONFLICT (material_id, user_id) DO UPDATE SET score = EXCLUDED.score";
        String updateAvgSql = "UPDATE materials SET average_rating = (SELECT AVG(score) FROM ratings WHERE material_id = ?) " +
                              "WHERE id = ?";
        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement pstmt1 = conn.prepareStatement(upsertSql)) {
                pstmt1.setLong(1, materialId);
                pstmt1.setLong(2, userId);
                pstmt1.setInt(3, score);
                pstmt1.executeUpdate();
            }
            try (PreparedStatement pstmt2 = conn.prepareStatement(updateAvgSql)) {
                pstmt2.setLong(1, materialId);
                pstmt2.setLong(2, materialId);
                pstmt2.executeUpdate();
            }
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
}