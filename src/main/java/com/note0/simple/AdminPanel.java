package com.note0.simple;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AdminPanel extends JPanel {

    private final SubjectDAO subjectDAO;
    private final MaterialDAO materialDAO;

    private JTable subjectTable;
    private DefaultTableModel subjectTableModel;
    private JTable materialTable;
    private DefaultTableModel materialTableModel;

    public AdminPanel(SubjectDAO subjectDAO, MaterialDAO materialDAO) {
        this.subjectDAO = subjectDAO;
        this.materialDAO = materialDAO;

        setLayout(new GridLayout(2, 1, 10, 10)); // Two main sections: Subjects and Materials
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Subjects Panel ---
        JPanel subjectsPanel = new JPanel(new BorderLayout(10, 10));
        subjectsPanel.setBorder(BorderFactory.createTitledBorder("Manage Subjects"));

        subjectTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Branch", "Semester"}, 0);
        subjectTable = new JTable(subjectTableModel);
        subjectsPanel.add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        
        JButton deleteSubjectButton = new JButton("Delete Selected Subject");
        subjectsPanel.add(deleteSubjectButton, BorderLayout.SOUTH);

        deleteSubjectButton.addActionListener(e -> deleteSubject());

        // --- Materials Panel ---
        JPanel materialsPanel = new JPanel(new BorderLayout(10, 10));
        materialsPanel.setBorder(BorderFactory.createTitledBorder("Manage Materials"));

        materialTableModel = new DefaultTableModel(new String[]{"ID", "Title", "Uploader", "Subject"}, 0);
        materialTable = new JTable(materialTableModel);
        materialsPanel.add(new JScrollPane(materialTable), BorderLayout.CENTER);

        JButton deleteMaterialButton = new JButton("Delete Selected Material");
        materialsPanel.add(deleteMaterialButton, BorderLayout.SOUTH);

        deleteMaterialButton.addActionListener(e -> deleteMaterial());
        
        // Add both main panels to the AdminPanel
        add(subjectsPanel);
        add(materialsPanel);

        loadSubjects();
        loadMaterials();
    }

    private void loadSubjects() {
        subjectTableModel.setRowCount(0);
        try {
            List<Subject> subjects = subjectDAO.getAllSubjects();
            for (Subject subject : subjects) {
                subjectTableModel.addRow(new Object[]{subject.getId(), subject.getName(), subject.getBranch(), subject.getSemester()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading subjects: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMaterials() {
        materialTableModel.setRowCount(0);
        try {
            List<Material> materials = materialDAO.getMaterials(null, null); // Get all materials
            for (Material material : materials) {
                materialTableModel.addRow(new Object[]{material.getId(), material.getTitle(), material.getUploaderName(), material.getSubjectName()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading materials: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) subjectTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this subject?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                subjectDAO.deleteSubject(id);
                JOptionPane.showMessageDialog(this, "Subject deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSubjects(); // Refresh the list
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting subject: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteMaterial() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a material to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) materialTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this material?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                materialDAO.deleteMaterial(id);
                JOptionPane.showMessageDialog(this, "Material deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMaterials(); // Refresh the list
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting material: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
