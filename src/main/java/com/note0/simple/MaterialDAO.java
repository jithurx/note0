package com.note0.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    public List<Material> getMaterials(String titleFilter, String subjectFilter) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT m.id, m.title, m.file_path, m.average_rating, u.full_name, s.name AS subject_name " +
            "FROM materials m " +
            "JOIN users u ON m.uploader_id = u.id " +
            "JOIN subjects s ON m.subject_id = s.id " +
            "WHERE m.approval_status = 'APPROVED'"
        );
        
        List<Object> params = new ArrayList<>();
        boolean hasWhere = true;

        if (titleFilter != null && !titleFilter.isBlank()) {
            sql.append(" AND LOWER(m.title) LIKE ?");
            params.add("%" + titleFilter.toLowerCase() + "%");
        }
        
        if (subjectFilter != null && !subjectFilter.isBlank() && !subjectFilter.equals("All Subjects")) {
            sql.append(" AND s.name = ?");
            params.add(subjectFilter);
        }

        sql.append(" ORDER BY m.id DESC");
        
        List<Material> materials = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
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
                    try {
                        material.setApprovalStatus(rs.getString("approval_status"));
                    } catch (SQLException e) {
                        // If approval_status column doesn't exist, set to APPROVED
                        material.setApprovalStatus("APPROVED");
                    }
                    materials.add(material);
                }
            }
        }
        return materials;
    }

    public void addMaterial(String title, String filePath, long subjectId, long uploaderId) throws SQLException {
        String sql = "INSERT INTO materials (title, file_path, subject_id, uploader_id, approval_status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, filePath);
            pstmt.setLong(3, subjectId);
            pstmt.setLong(4, uploaderId);
            pstmt.setString(5, "PENDING");
            pstmt.executeUpdate();
        }
    }

    public void deleteMaterial(long materialId) throws SQLException {
        String sql = "DELETE FROM materials WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, materialId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Deletes a material from the database only (keeps the file in Cloudinary).
     * @param materialId The ID of the material to delete
     * @param cloudinaryService The CloudinaryService instance (not used, kept for compatibility)
     * @return true if database deletion was successful
     */
    public boolean deleteMaterialWithFile(long materialId, CloudinaryService cloudinaryService) throws SQLException {
        // Delete from database only - keep the file in Cloudinary
        deleteMaterial(materialId);
        return true;
    }

    public Material getMaterialById(long id) throws SQLException {
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, m.approval_status, u.full_name, s.name AS subject_name " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "WHERE m.id = ?";
        Material material = null;
        try (Connection conn = DatabaseManager.getConnection();
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
                    try {
                        material.setApprovalStatus(rs.getString("approval_status"));
                    } catch (SQLException e) {
                        // If approval_status column doesn't exist, set to APPROVED
                        material.setApprovalStatus("APPROVED");
                    }
                }
            }
        }
        return material;
    }

    public List<Material> getRecentMaterials(int limit) throws SQLException {
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, u.full_name, s.name AS subject_name " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "WHERE m.approval_status = 'APPROVED' " +
                     "ORDER BY m.id DESC " +
                     "LIMIT ?";
        
        List<Material> materials = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setId(rs.getLong("id"));
                    material.setTitle(rs.getString("title"));
                    material.setFilePath(rs.getString("file_path"));
                    material.setUploaderName(rs.getString("full_name"));
                    material.setSubjectName(rs.getString("subject_name"));
                    material.setAverageRating(rs.getDouble("average_rating"));
                    try {
                        material.setApprovalStatus(rs.getString("approval_status"));
                    } catch (SQLException e) {
                        // If approval_status column doesn't exist, set to APPROVED
                        material.setApprovalStatus("APPROVED");
                    }
                    materials.add(material);
                }
            }
        }
        return materials;
    }
    
    public List<Material> getTopRatedMaterials(int limit) throws SQLException {
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, u.full_name, s.name AS subject_name " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "WHERE m.average_rating > 0 AND m.approval_status = 'APPROVED' " +
                     "ORDER BY m.average_rating DESC, m.id DESC " +
                     "LIMIT ?";
        
        List<Material> materials = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setId(rs.getLong("id"));
                    material.setTitle(rs.getString("title"));
                    material.setFilePath(rs.getString("file_path"));
                    material.setUploaderName(rs.getString("full_name"));
                    material.setSubjectName(rs.getString("subject_name"));
                    material.setAverageRating(rs.getDouble("average_rating"));
                    try {
                        material.setApprovalStatus(rs.getString("approval_status"));
                    } catch (SQLException e) {
                        // If approval_status column doesn't exist, set to APPROVED
                        material.setApprovalStatus("APPROVED");
                    }
                    materials.add(material);
                }
            }
        }
        return materials;
    }
    
    public List<Material> getPendingMaterials() throws SQLException {
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, m.approval_status, u.full_name, s.name AS subject_name " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "WHERE m.approval_status = 'PENDING' " +
                     "ORDER BY m.id DESC";
        
        List<Material> materials = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setId(rs.getLong("id"));
                    material.setTitle(rs.getString("title"));
                    material.setFilePath(rs.getString("file_path"));
                    material.setUploaderName(rs.getString("full_name"));
                    material.setSubjectName(rs.getString("subject_name"));
                    material.setAverageRating(rs.getDouble("average_rating"));
                    try {
                        material.setApprovalStatus(rs.getString("approval_status"));
                    } catch (SQLException e) {
                        // If approval_status column doesn't exist, set to APPROVED
                        material.setApprovalStatus("APPROVED");
                    }
                    materials.add(material);
                }
            }
        }
        return materials;
    }
    
    public void updateApprovalStatus(long materialId, String status) throws SQLException {
        String sql = "UPDATE materials SET approval_status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setLong(2, materialId);
            pstmt.executeUpdate();
        }
    }
    
    public List<Material> getAllMaterialsForAdmin() throws SQLException {
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, m.approval_status, u.full_name, s.name AS subject_name " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "ORDER BY m.id DESC";
        
        List<Material> materials = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setId(rs.getLong("id"));
                    material.setTitle(rs.getString("title"));
                    material.setFilePath(rs.getString("file_path"));
                    material.setUploaderName(rs.getString("full_name"));
                    material.setSubjectName(rs.getString("subject_name"));
                    material.setAverageRating(rs.getDouble("average_rating"));
                    try {
                        material.setApprovalStatus(rs.getString("approval_status"));
                    } catch (SQLException e) {
                        // If approval_status column doesn't exist, set to APPROVED
                        material.setApprovalStatus("APPROVED");
                    }
                    materials.add(material);
                }
            }
        }
        return materials;
    }
    
    /**
     * Adds or updates a rating for a material by a user.
     * @param materialId The ID of the material being rated
     * @param userId The ID of the user giving the rating
     * @param rating The rating score (1-5)
     * @throws SQLException if a database error occurs
     */
    public void addOrUpdateRating(long materialId, long userId, int rating) throws SQLException {
        // First, check if user has already rated this material
        String checkSql = "SELECT id FROM ratings WHERE material_id = ? AND user_id = ?";
        boolean hasRated = false;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setLong(1, materialId);
            pstmt.setLong(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                hasRated = rs.next();
            }
        }
        
        if (hasRated) {
            // Update existing rating
            String updateSql = "UPDATE ratings SET score = ? WHERE material_id = ? AND user_id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setInt(1, rating);
                pstmt.setLong(2, materialId);
                pstmt.setLong(3, userId);
                pstmt.executeUpdate();
            }
        } else {
            // Insert new rating
            String insertSql = "INSERT INTO ratings (material_id, user_id, score) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setLong(1, materialId);
                pstmt.setLong(2, userId);
                pstmt.setInt(3, rating);
                pstmt.executeUpdate();
            }
        }
        
        // Update the average rating for the material
        updateAverageRating(materialId);
    }
    
    /**
     * Updates the average rating for a material based on all its ratings.
     * @param materialId The ID of the material
     * @throws SQLException if a database error occurs
     */
    private void updateAverageRating(long materialId) throws SQLException {
        String sql = "UPDATE materials SET average_rating = " +
                     "(SELECT COALESCE(AVG(score), 0) FROM ratings WHERE material_id = ?) " +
                     "WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, materialId);
            pstmt.setLong(2, materialId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Gets the rating given by a specific user for a material.
     * @param materialId The ID of the material
     * @param userId The ID of the user
     * @return The rating score (1-5) or 0 if not rated
     * @throws SQLException if a database error occurs
     */
    public int getUserRating(long materialId, long userId) throws SQLException {
        String sql = "SELECT score FROM ratings WHERE material_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, materialId);
            pstmt.setLong(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("score");
                }
            }
        }
        return 0; // No rating found
    }
    
    /**
     * Gets materials uploaded by a specific user.
     * @param userId The ID of the user
     * @return List of materials uploaded by the user
     * @throws SQLException if a database error occurs
     */
    public List<Material> getMaterialsByUser(long userId) throws SQLException {
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, m.approval_status, u.full_name, s.name AS subject_name " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "WHERE m.uploader_id = ? " +
                     "ORDER BY m.id DESC";
        
        List<Material> materials = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setId(rs.getLong("id"));
                    material.setTitle(rs.getString("title"));
                    material.setFilePath(rs.getString("file_path"));
                    material.setUploaderName(rs.getString("full_name"));
                    material.setSubjectName(rs.getString("subject_name"));
                    material.setAverageRating(rs.getDouble("average_rating"));
                    try {
                        material.setApprovalStatus(rs.getString("approval_status"));
                    } catch (SQLException e) {
                        // If approval_status column doesn't exist, set to APPROVED
                        material.setApprovalStatus("APPROVED");
                    }
                    materials.add(material);
                }
            }
        }
        return materials;
    }
}