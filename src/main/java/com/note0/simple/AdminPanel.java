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
        materialsPanel.setBorder(BorderFactory.createTitledBorder("Review Pending Materials"));

        materialTableModel = new DefaultTableModel(new String[]{"ID", "Title", "Uploader", "Subject", "Status"}, 0);
        materialTable = new JTable(materialTableModel);
        materialsPanel.add(new JScrollPane(materialTable), BorderLayout.CENTER);

        JPanel materialButtonPanel = new JPanel(new FlowLayout());
        JButton approveButton = new JButton("Approve Selected");
        JButton rejectButton = new JButton("Reject Selected");
        JButton deleteMaterialButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");
        
        materialButtonPanel.add(approveButton);
        materialButtonPanel.add(rejectButton);
        materialButtonPanel.add(deleteMaterialButton);
        materialButtonPanel.add(refreshButton);
        materialsPanel.add(materialButtonPanel, BorderLayout.SOUTH);

        approveButton.addActionListener(e -> approveMaterial());
        rejectButton.addActionListener(e -> rejectMaterial());
        deleteMaterialButton.addActionListener(e -> deleteMaterial());
        refreshButton.addActionListener(e -> loadPendingMaterials());
        
        // Add both main panels to the AdminPanel
        add(subjectsPanel);
        add(materialsPanel);

        loadSubjects();
        loadPendingMaterials();
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

    private void loadPendingMaterials() {
        materialTableModel.setRowCount(0);
        try {
            List<Material> materials = materialDAO.getPendingMaterials();
            for (Material material : materials) {
                materialTableModel.addRow(new Object[]{material.getId(), material.getTitle(), material.getUploaderName(), material.getSubjectName(), material.getApprovalStatus()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading pending materials: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
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
                loadPendingMaterials(); // Refresh the list
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting material: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void approveMaterial() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a material to approve.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) materialTableModel.getValueAt(selectedRow, 0);
        String title = (String) materialTableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to approve '" + title + "'?", "Confirm Approval", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                materialDAO.updateApprovalStatus(id, "APPROVED");
                JOptionPane.showMessageDialog(this, "Material approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingMaterials(); // Refresh the list
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error approving material: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void rejectMaterial() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a material to reject.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) materialTableModel.getValueAt(selectedRow, 0);
        String title = (String) materialTableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to reject '" + title + "'?", "Confirm Rejection", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                materialDAO.updateApprovalStatus(id, "REJECTED");
                JOptionPane.showMessageDialog(this, "Material rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPendingMaterials(); // Refresh the list
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error rejecting material: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
