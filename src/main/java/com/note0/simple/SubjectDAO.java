package com.note0.simple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SubjectDAO handles all database CRUD (Create, Read, Update, Delete) operations for Subjects.
 */
public class SubjectDAO {

    /**
     * Retrieves all subjects from the database, ordered by semester, branch, and name.
     * @return A list of all Subject objects.
     * @throws SQLException if a database error occurs.
     */
    public List<Subject> getAllSubjects() throws SQLException {
        // Updated SQL to include new columns and a more logical ordering
        String sql = "SELECT id, name, branch, semester FROM subjects ORDER BY semester, branch, name";
        List<Subject> subjects = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setId(rs.getLong("id"));
                subject.setName(rs.getString("name"));
                subject.setBranch(rs.getString("branch"));
                subject.setSemester(rs.getInt("semester"));
                subjects.add(subject);
            }
        }
        return subjects;
    }

    /**
     * Adds a new subject to the database.
     * @param name The name of the new subject.
     * @param branch The branch it belongs to.
     * @param semester The semester it belongs to.
     * @throws SQLException if a database error occurs.
     */
    public void addSubject(String name, String branch, int semester) throws SQLException {
        String sql = "INSERT INTO subjects (name, branch, semester) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, branch);
            pstmt.setInt(3, semester);
            pstmt.executeUpdate();
        }
    }

    /**
     * Updates an existing subject's details in the database.
     * @param id The ID of the subject to update.
     * @param newName The new name for the subject.
     * @param newBranch The new branch for the subject.
     * @param newSemester The new semester for the subject.
     * @throws SQLException if a database error occurs.
     */
    public void updateSubject(long id, String newName, String newBranch, int newSemester) throws SQLException {
        String sql = "UPDATE subjects SET name = ?, branch = ?, semester = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newBranch);
            pstmt.setInt(3, newSemester);
            pstmt.setLong(4, id);
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a subject from the database by its ID.
     * @param id The ID of the subject to delete.
     * @throws SQLException if a database error occurs.
     */
    public void deleteSubject(long id) throws SQLException {
        String sql = "DELETE FROM subjects WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
}