package com.note0.simple;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * The admin panel for managing subjects.
 * Provides a full CRUD (Create, Read, Update, Delete) interface.
 */
public class AdminPanel extends JPanel {

    private final SubjectDAO subjectDAO;
    private final DashboardPanel dashboardPanel; // A reference to the dashboard to refresh it

    private JTable subjectTable;
    private DefaultTableModel tableModel;

    // Form components
    private JTextField nameField = new JTextField(20);
    private JTextField branchField = new JTextField(20);
    private JSpinner semesterSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
    private JButton addButton = new JButton("Add Subject");
    private JButton updateButton = new JButton("Update Selected");
    private JButton deleteButton = new JButton("Delete Selected");
    private JButton clearButton = new JButton("Clear Form");

    public AdminPanel(DashboardPanel dashboardPanel, SubjectDAO subjectDAO) {
        this.dashboardPanel = dashboardPanel;
        this.subjectDAO = subjectDAO;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- CENTER: Table of Subjects ---
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Branch", "Semester"}, 0);
        subjectTable = new JTable(tableModel);
        add(new JScrollPane(subjectTable), BorderLayout.CENTER);

        // --- BOTTOM: Form for Adding/Editing ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Subject Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; formPanel.add(nameField, gbc);

        gbc.gridwidth = 1; // Reset gridwidth
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Branch:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(branchField, gbc);

        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(semesterSpinner, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);

        // --- Attach Event Listeners ---
        subjectTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelectedRow();
        });
        
        addButton.addActionListener(e -> addSubject());
        updateButton.addActionListener(e -> updateSubject());
        deleteButton.addActionListener(e -> deleteSubject());
        clearButton.addActionListener(e -> clearForm());

        // --- Load Initial Data ---
        loadSubjects();
    }

    private void loadSubjects() {
        tableModel.setRowCount(0); // Clear table
        try {
            List<Subject> subjects = subjectDAO.getAllSubjects();
            for (Subject subject : subjects) {
                tableModel.addRow(new Object[]{subject.getId(), subject.getName(), subject.getBranch(), subject.getSemester()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading subjects: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        subjectTable.clearSelection();
        nameField.setText("");
        branchField.setText("");
        semesterSpinner.setValue(1);
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow >= 0) {
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            branchField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            semesterSpinner.setValue(tableModel.getValueAt(selectedRow, 3));
        }
    }

    private void addSubject() {
        try {
            subjectDAO.addSubject(nameField.getText(), branchField.getText(), (int) semesterSpinner.getValue());
            JOptionPane.showMessageDialog(this, "Subject added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSubjects(); // Refresh this panel's table
            dashboardPanel.refreshData(); // CORRECTED LINE: Refresh the main dashboard
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding subject: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a subject to update.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        try {
            subjectDAO.updateSubject(id, nameField.getText(), branchField.getText(), (int) semesterSpinner.getValue());
            JOptionPane.showMessageDialog(this, "Subject updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSubjects();
            dashboardPanel.refreshData(); // CORRECTED LINE: Refresh the main dashboard
            clearForm();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating subject: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this subject?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                subjectDAO.deleteSubject(id);
                JOptionPane.showMessageDialog(this, "Subject deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSubjects();
                dashboardPanel.refreshData(); // CORRECTED LINE: Refresh the main dashboard
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting subject: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}