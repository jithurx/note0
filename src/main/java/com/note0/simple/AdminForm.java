package com.note0.simple;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminForm extends JFrame {

    private final MaterialDAO materialDAO;
    private final SubjectDAO subjectDAO;
    private final UserDAO userDAO;

    public AdminForm(SubjectDAO subjectDAO, UserDAO userDAO, MaterialDAO materialDAO) {
        this.subjectDAO = subjectDAO;
        this.userDAO = userDAO;
        this.materialDAO = materialDAO;

        setTitle("Admin Panel");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Subjects Panel
        JPanel subjectPanel = createSubjectPanel();
        tabbedPane.addTab("Manage Subjects", subjectPanel);

        // Users Panel
        JPanel userPanel = createUserPanel();
        tabbedPane.addTab("Manage Users", userPanel);

        // Materials Panel
        JPanel materialPanel = createMaterialPanel();
        tabbedPane.addTab("Manage Materials", materialPanel);

        add(tabbedPane);
    }

    private JPanel createSubjectPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel subjectTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Branch", "Semester"}, 0);
        JTable subjectTable = new JTable(subjectTableModel);
        loadSubjects(subjectTableModel);

        JButton addSubjectButton = new JButton("Add New Subject");
        JButton editSubjectButton = new JButton("Edit Selected Subject");
        JButton deleteSubjectButton = new JButton("Delete Selected Subject");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addSubjectButton);
        buttonPanel.add(editSubjectButton);
        buttonPanel.add(deleteSubjectButton);

        panel.add(new JScrollPane(subjectTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addSubjectButton.addActionListener(e -> {
            String subjectName = JOptionPane.showInputDialog(this, "Enter new subject name:");
            if (subjectName != null && !subjectName.isBlank()) {
                try {
                    subjectDAO.addSubject(subjectName, "", 1); // Default branch and semester
                    loadSubjects(subjectTableModel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error adding subject: " + ex.getMessage());
                }
            }
        });

        editSubjectButton.addActionListener(e -> {
            int selectedRow = subjectTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a subject to edit.");
                return;
            }
            long subjectId = (long) subjectTableModel.getValueAt(selectedRow, 0);
            String currentName = (String) subjectTableModel.getValueAt(selectedRow, 1);

            String newName = JOptionPane.showInputDialog(this, "Enter new name for the subject:", currentName);
            if (newName != null && !newName.isBlank()) {
                try {
                    subjectDAO.updateSubject(subjectId, newName, "", 1); // Default branch and semester
                    loadSubjects(subjectTableModel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error updating subject: " + ex.getMessage());
                }
            }
        });

        deleteSubjectButton.addActionListener(e -> {
            int selectedRow = subjectTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a subject to delete.");
                return;
            }
            long subjectId = (long) subjectTableModel.getValueAt(selectedRow, 0);
            String subjectName = (String) subjectTableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete '" + subjectName + "'?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    subjectDAO.deleteSubject(subjectId);
                    loadSubjects(subjectTableModel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting subject: " + ex.getMessage());
                }
            }
        });

        return panel;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel userTableModel = new DefaultTableModel(new String[]{"ID", "Username", "Role", "Semester"}, 0);
        JTable userTable = new JTable(userTableModel);
        loadUsers(userTableModel);

        JButton deleteUserButton = new JButton("Delete Selected User");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteUserButton);
        
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        deleteUserButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a user to delete.");
                return;
            }
            long userId = (long) userTableModel.getValueAt(selectedRow, 0);
            String username = (String) userTableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete user '" + username + "'?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    userDAO.deleteUser(userId);
                    loadUsers(userTableModel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
                }
            }
        });

        return panel;
    }

    private JPanel createMaterialPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel materialTableModel = new DefaultTableModel(new String[]{"ID", "Title", "Uploader"}, 0);
        JTable materialTable = new JTable(materialTableModel);
        loadMaterials(materialTableModel);

        JButton deleteMaterialButton = new JButton("Delete Selected Material");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteMaterialButton);

        panel.add(new JScrollPane(materialTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        deleteMaterialButton.addActionListener(e -> {
            int selectedRow = materialTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a material to delete.");
                return;
            }
            long materialId = (long) materialTableModel.getValueAt(selectedRow, 0);
            String materialTitle = (String) materialTableModel.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete material '" + materialTitle + "'?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    materialDAO.deleteMaterial(materialId);
                    loadMaterials(materialTableModel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting material: " + ex.getMessage());
                }
            }
        });

        return panel;
    }

    private void loadSubjects(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Subject> subjects = subjectDAO.getAllSubjects();
            for (Subject subject : subjects) {
                model.addRow(new Object[]{subject.getId(), subject.getName(), subject.getBranch(), subject.getSemester()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load subjects.");
        }
    }

    private void loadUsers(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                model.addRow(new Object[]{user.getId(), user.getUsername(), user.getRole(), user.getSemester()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load users.");
        }
    }

    private void loadMaterials(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Material> materials = materialDAO.getAllMaterials();
            for (Material material : materials) {
                model.addRow(new Object[]{material.getId(), material.getTitle(), material.getUploader().getUsername()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load materials.");
        }
    }
}