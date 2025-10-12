package com.note0.simple;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminForm extends JFrame {

    private MaterialDAO materialDAO; // Still needed to GET subjects
    private SubjectDAO subjectDAO;   // The new DAO for modifying subjects

    private JTable subjectTable;
    private DefaultTableModel tableModel;

    public AdminForm(SubjectDAO subjectDAO) {
        this.materialDAO = new MaterialDAO();
        this.subjectDAO = subjectDAO;

        setTitle("Admin Panel - Manage Subjects");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // IMPORTANT: Only closes this window
        setLocationRelativeTo(null);

        // --- UI Components ---
        tableModel = new DefaultTableModel(new String[]{"ID", "Name"}, 0);
        subjectTable = new JTable(tableModel);

        JButton addButton = new JButton("Add New Subject");
        JButton editButton = new JButton("Edit Selected Subject");
        JButton deleteButton = new JButton("Delete Selected Subject");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Load Data ---
        loadSubjects();

        // --- Action Listeners ---
        addButton.addActionListener(e -> {
            String subjectName = JOptionPane.showInputDialog(this, "Enter new subject name:");
            if (subjectName != null && !subjectName.isBlank()) {
                try {
                    subjectDAO.addSubject(subjectName, "", 1); // Default branch and semester 1
                    loadSubjects(); // Refresh table
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error adding subject: " + ex.getMessage());
                }
            }
        });

        editButton.addActionListener(e -> {
            int selectedRow = subjectTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a subject to edit.");
                return;
            }
            long subjectId = (long) tableModel.getValueAt(selectedRow, 0);
            String currentName = (String) tableModel.getValueAt(selectedRow, 1);
            
            String newName = JOptionPane.showInputDialog(this, "Enter new name for the subject:", currentName);
            if (newName != null && !newName.isBlank()) {
                try {
                    subjectDAO.updateSubject(subjectId, newName, "", 1); // Default branch and semester 1
                    loadSubjects(); // Refresh table
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error updating subject: " + ex.getMessage());
                }
            }
        });
        
        deleteButton.addActionListener(e -> {
            int selectedRow = subjectTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a subject to delete.");
                return;
            }
            long subjectId = (long) tableModel.getValueAt(selectedRow, 0);
            String subjectName = (String) tableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete '" + subjectName + "'?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    subjectDAO.deleteSubject(subjectId);
                    loadSubjects(); // Refresh table
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting subject: " + ex.getMessage());
                }
            }
        });
    }

    private void loadSubjects() {
        tableModel.setRowCount(0);
        try {
            List<Subject> subjects = subjectDAO.getAllSubjects();
            for (Subject subject : subjects) {
                tableModel.addRow(new Object[]{subject.getId(), subject.getName(), subject.getBranch(), subject.getSemester()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load subjects.");
        }
    }
}