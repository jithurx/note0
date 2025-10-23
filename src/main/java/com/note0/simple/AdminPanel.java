package com.note0.simple;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AdminPanel extends JPanel {

    private final SubjectDAO subjectDAO;
    private final MaterialDAO materialDAO;
    private final CloudinaryService cloudinaryService;

    private JTable subjectTable;
    private DefaultTableModel subjectTableModel;
    private JTable materialTable;
    private DefaultTableModel materialTableModel;

    public AdminPanel(SubjectDAO subjectDAO, MaterialDAO materialDAO, CloudinaryService cloudinaryService) {
        this.subjectDAO = subjectDAO;
        this.materialDAO = materialDAO;
        this.cloudinaryService = cloudinaryService;

        setLayout(new GridLayout(2, 1, 15, 15)); // Two main sections: Subjects and Materials
        setBackground(UITheme.APP_BACKGROUND);
        setBorder(UITheme.APP_PADDING);

        // --- Subjects Panel ---
        JPanel subjectsPanel = new JPanel(new BorderLayout(10, 10));
        subjectsPanel.setBackground(UITheme.CARD_BACKGROUND);
        subjectsPanel.setBorder(UITheme.createShadowBorder());

        // Title Label for Subjects
        JLabel subjectTitle = new JLabel("Manage Subjects");
        subjectTitle.setFont(UITheme.HEADING_FONT);
        subjectTitle.setForeground(UITheme.TEXT_COLOR);
        subjectTitle.setHorizontalAlignment(SwingConstants.CENTER);
        subjectTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        subjectsPanel.add(subjectTitle, BorderLayout.NORTH);

        subjectTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Branch", "Semester"}, 0);
        subjectTable = new JTable(subjectTableModel);

        // --- APPLY TABLE STYLES ---
        subjectTable.setFont(UITheme.TABLE_BODY_FONT);
        subjectTable.getTableHeader().setFont(UITheme.TABLE_HEADER_FONT);
        subjectTable.setRowHeight(UITheme.TABLE_ROW_HEIGHT);
        // -------------------------
        
        subjectsPanel.add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        
        // Subject management buttons
        JPanel subjectButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        subjectButtonPanel.setBackground(UITheme.CARD_BACKGROUND);
        JButton addSubjectButton = new JButton("Add New");
        UITheme.stylePrimaryButton(addSubjectButton);
        JButton deleteSubjectButton = new JButton("Delete Selected");
        UITheme.styleDangerButton(deleteSubjectButton);
        JButton refreshSubjectsButton = new JButton("Refresh");
        UITheme.styleSecondaryButton(refreshSubjectsButton);
        
        subjectButtonPanel.add(addSubjectButton);
        subjectButtonPanel.add(deleteSubjectButton);
        subjectButtonPanel.add(refreshSubjectsButton);
        subjectsPanel.add(subjectButtonPanel, BorderLayout.SOUTH);

        addSubjectButton.addActionListener(e -> addSubject());
        deleteSubjectButton.addActionListener(e -> deleteSubject());
        refreshSubjectsButton.addActionListener(e -> loadSubjects());

        // --- Materials Panel ---
        JPanel materialsPanel = new JPanel(new BorderLayout(10, 10));
        materialsPanel.setBackground(UITheme.CARD_BACKGROUND);
        materialsPanel.setBorder(UITheme.createShadowBorder());

        // Title Label for Materials
        JLabel materialTitle = new JLabel("Manage Materials");
        materialTitle.setFont(UITheme.HEADING_FONT);
        materialTitle.setForeground(UITheme.TEXT_COLOR);
        materialTitle.setHorizontalAlignment(SwingConstants.CENTER);
        materialTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        materialsPanel.add(materialTitle, BorderLayout.NORTH);

        materialTableModel = new DefaultTableModel(new String[]{"ID", "Title", "Uploader", "Subject", "Status"}, 0);
        materialTable = new JTable(materialTableModel);

        // --- APPLY TABLE STYLES ---
        materialTable.setFont(UITheme.TABLE_BODY_FONT);
        materialTable.getTableHeader().setFont(UITheme.TABLE_HEADER_FONT);
        materialTable.setRowHeight(UITheme.TABLE_ROW_HEIGHT);
        // -------------------------

        materialsPanel.add(new JScrollPane(materialTable), BorderLayout.CENTER);

        JPanel materialButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        materialButtonPanel.setBackground(UITheme.CARD_BACKGROUND);
        JButton approveButton = new JButton("Approve");
        UITheme.stylePrimaryButton(approveButton);
        JButton rejectButton = new JButton("Reject");
        UITheme.styleDangerButton(rejectButton);
        JButton deleteMaterialButton = new JButton("Delete");
        UITheme.styleDangerButton(deleteMaterialButton);
        JButton showPendingButton = new JButton("Show Pending");
        UITheme.styleSecondaryButton(showPendingButton);
        JButton showAllButton = new JButton("Show All");
        UITheme.styleSecondaryButton(showAllButton);
        
        materialButtonPanel.add(showPendingButton);
        materialButtonPanel.add(showAllButton);
        materialButtonPanel.add(approveButton);
        materialButtonPanel.add(rejectButton);
        materialButtonPanel.add(deleteMaterialButton);
        materialsPanel.add(materialButtonPanel, BorderLayout.SOUTH);

        approveButton.addActionListener(e -> approveMaterial());
        rejectButton.addActionListener(e -> rejectMaterial());
        deleteMaterialButton.addActionListener(e -> deleteMaterial());
        showPendingButton.addActionListener(e -> loadPendingMaterials());
        showAllButton.addActionListener(e -> loadAllMaterials());
        
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
    
    private void loadAllMaterials() {
        materialTableModel.setRowCount(0);
        try {
            List<Material> materials = materialDAO.getAllMaterialsForAdmin();
            for (Material material : materials) {
                materialTableModel.addRow(new Object[]{material.getId(), material.getTitle(), material.getUploaderName(), material.getSubjectName(), material.getApprovalStatus()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading all materials: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSubject() {
        // Create a dialog for adding a new subject
        JDialog addSubjectDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Subject", true);
        addSubjectDialog.setLayout(new BorderLayout());
        addSubjectDialog.setSize(400, 300);
        addSubjectDialog.setLocationRelativeTo(this);
        addSubjectDialog.getContentPane().setBackground(UITheme.APP_BACKGROUND);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.CARD_BACKGROUND);
        formPanel.setBorder(UITheme.APP_PADDING);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Subject name field
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        JLabel nameLabel = new JLabel("Subject Name:");
        nameLabel.setForeground(UITheme.TEXT_COLOR);
        nameLabel.setFont(UITheme.LABEL_FONT);
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Branch field
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel branchLabel = new JLabel("Branch:");
        branchLabel.setForeground(UITheme.TEXT_COLOR);
        branchLabel.setFont(UITheme.LABEL_FONT);
        formPanel.add(branchLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField branchField = new JTextField(20);
        formPanel.add(branchField, gbc);

        // Semester field
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JLabel semLabel = new JLabel("Semester:");
        semLabel.setForeground(UITheme.TEXT_COLOR);
        semLabel.setFont(UITheme.LABEL_FONT);
        formPanel.add(semLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JSpinner semesterSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        formPanel.add(semesterSpinner, gbc);

        addSubjectDialog.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.CARD_BACKGROUND);
        JButton saveButton = new JButton("Save");
        UITheme.stylePrimaryButton(saveButton);
        JButton cancelButton = new JButton("Cancel");
        UITheme.styleSecondaryButton(cancelButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        addSubjectDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Event handlers
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String branch = branchField.getText().trim();
            int semester = (Integer) semesterSpinner.getValue();

            if (name.isEmpty() || branch.isEmpty()) {
                JOptionPane.showMessageDialog(addSubjectDialog, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                subjectDAO.addSubject(name, branch, semester);
                JOptionPane.showMessageDialog(addSubjectDialog, "Subject added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                addSubjectDialog.dispose();
                loadSubjects(); // Refresh the subjects list
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(addSubjectDialog, "Error adding subject: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addSubjectDialog.dispose());

        addSubjectDialog.setVisible(true);
    }

    private void deleteSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Long id = (Long) subjectTableModel.getValueAt(selectedRow, 0);
        String subjectName = (String) subjectTableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the subject '" + subjectName + "'?\n\n" +
            "This will also delete all materials associated with this subject.", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
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
        String materialTitle = (String) materialTableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the material '" + materialTitle + "'?\n\n" +
            "This will remove the material from the database.\n" +
            "The uploaded file will remain in Cloudinary storage.", 
            "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = materialDAO.deleteMaterialWithFile(id, cloudinaryService);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Material deleted from database successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting material from database.", "Error", JOptionPane.ERROR_MESSAGE);
                }
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

