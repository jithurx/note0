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
}