package com.note0.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAO {

    private final UserDAO userDAO;

    public MaterialDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<Material> getMaterials(String titleFilter, String subjectFilter, int userSemester) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT m.id, m.title, m.file_path, m.average_rating, u.username, s.name AS subject_name, s.semester " +
            "FROM materials m " +
            "JOIN users u ON m.uploader_id = u.id " +
            "JOIN subjects s ON m.subject_id = s.id"
        );
        
        List<Object> params = new ArrayList<>();
        boolean hasWhere = false;

        if (titleFilter != null && !titleFilter.isBlank()) {
            sql.append(" WHERE LOWER(m.title) LIKE ?");
            params.add("%" + titleFilter.toLowerCase() + "%");
            hasWhere = true;
        }
        
        if (subjectFilter != null && !subjectFilter.isBlank() && !subjectFilter.equals("All Subjects")) {
            sql.append(hasWhere ? " AND" : " WHERE").append(" s.name = ?");
            params.add(subjectFilter);
            hasWhere = true;
        }

        sql.append(hasWhere ? " AND" : " WHERE").append(" s.semester = ?");
        params.add(userSemester);

        sql.append(" ORDER BY m.id DESC");
        
        return getMaterialsFromQuery(sql.toString(), params);
    }

    public void addMaterial(String title, String filePath, long subjectId, long uploaderId) throws SQLException {
        String sql = "INSERT INTO materials (title, file_path, subject_id, uploader_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, filePath);
            pstmt.setLong(3, subjectId);
            pstmt.setLong(4, uploaderId);
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
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, u.username, s.name AS subject_name " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "WHERE m.id = ?";
        List<Material> materials = getMaterialsFromQuery(sql, List.of(id));
        return materials.isEmpty() ? null : materials.get(0);
    }

    public List<Material> getRecentMaterials(int limit, int userSemester) throws SQLException {
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, u.username, s.name AS subject_name " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "WHERE s.semester = ? " +
                     "ORDER BY m.id DESC " +
                     "LIMIT ?";
        
        return getMaterialsFromQuery(sql, List.of(userSemester, limit));
    }
    
    public List<Material> getTopRatedMaterials(int limit) throws SQLException {
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, u.username, s.name AS subject_name " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "WHERE m.average_rating > 0 " +
                     "ORDER BY m.average_rating DESC, m.id DESC " +
                     "LIMIT ?";
        
        return getMaterialsFromQuery(sql, List.of(limit));
    }
    
    public List<Material> getAllMaterials() throws SQLException {
        String sql = "SELECT m.id, m.title, m.file_path, m.average_rating, u.username, s.name AS subject_name " +
                     "FROM materials m " +
                     "JOIN users u ON m.uploader_id = u.id " +
                     "JOIN subjects s ON m.subject_id = s.id " +
                     "ORDER BY m.id DESC";
        
        return getMaterialsFromQuery(sql, new ArrayList<>());
    }

    private List<Material> getMaterialsFromQuery(String sql, List<Object> params) throws SQLException {
        List<Material> materials = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setId(rs.getLong("id"));
                    material.setTitle(rs.getString("title"));
                    material.setFilePath(rs.getString("file_path"));
                    material.setSubjectName(rs.getString("subject_name"));
                    material.setAverageRating(rs.getDouble("average_rating"));

                    User uploader = userDAO.getUserByUsername(rs.getString("username"));
                    material.setUploader(uploader);

                    materials.add(material);
                }
            }
        }
        return materials;
    }
}
