package com.note0.simple;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final User loggedInUser;
    private final MaterialDAO materialDAO;
    private final SubjectDAO subjectDAO;
    private final CloudinaryService cloudinaryService;

    private List<Subject> allSubjects;
    private Map<String, Long> subjectNameToIdMap = new HashMap<>();

    private JTable materialsTable;
    private DefaultTableModel tableModel;

    public DashboardPanel(MainFrame mainFrame, User user, MaterialDAO materialDAO, SubjectDAO subjectDAO, CloudinaryService cloudinaryService) {
        this.mainFrame = mainFrame;
        this.loggedInUser = user;
        this.materialDAO = materialDAO;
        this.subjectDAO = subjectDAO;
        this.cloudinaryService = cloudinaryService;

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Browse Materials", createBrowsePanel());
        tabbedPane.addTab("Upload Material", createUploadPanel());

        add(tabbedPane, BorderLayout.CENTER);
        
        JButton backButton = new JButton("Back to Feed");
        backButton.addActionListener(e -> mainFrame.showFeedPanel(loggedInUser));
        add(backButton, BorderLayout.SOUTH);

        loadAndCacheSubjects(); // Load subjects once
    }

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JComboBox<String> subjectFilterComboBox = new JComboBox<>();
        JButton filterButton = new JButton("Search / Refresh");

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Subject:"));
        filterPanel.add(subjectFilterComboBox);
        filterPanel.add(filterButton);

        // Materials Table
        String[] columnNames = {"Title", "Subject", "Rating", "Uploader"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        materialsTable = new JTable(tableModel);
        materialsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = materialsTable.rowAtPoint(evt.getPoint());
                    long materialId = (long) materialsTable.getModel().getValueAt(row, -1); // Hidden ID column
                    try {
                        Material material = materialDAO.getMaterialById(materialId);
                        if(material != null) openMaterial(material);
                    } catch(SQLException e) {
                        JOptionPane.showMessageDialog(panel, "Error retrieving material details.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Populate filters and initial data
        loadAndCacheSubjects();
        populateSubjectFilter(subjectFilterComboBox);
        loadMaterials(searchField.getText(), (String) subjectFilterComboBox.getSelectedItem());

        filterButton.addActionListener(e -> loadMaterials(searchField.getText(), (String) subjectFilterComboBox.getSelectedItem()));

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(materialsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUploadPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField titleField = new JTextField(20);
        JComboBox<String> uploadSubjectComboBox = new JComboBox<>();
        JButton chooseFileButton = new JButton("Choose File");
        JLabel selectedFileLabel = new JLabel("No file selected.");
        final File[] selectedFile = {null}; // Using an array to be final and mutable
        JButton uploadButton = new JButton("Upload");

        populateSubjectFilter(uploadSubjectComboBox);
        uploadSubjectComboBox.removeItem("All Subjects"); // Can't upload to 'All'

        // Layout
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; panel.add(titleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panel.add(uploadSubjectComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(chooseFileButton, gbc);
        gbc.gridx = 1; gbc.gridy = 2; panel.add(selectedFileLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; panel.add(uploadButton, gbc);

        // Actions
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedFile[0] = fileChooser.getSelectedFile();
                selectedFileLabel.setText(selectedFile[0].getName());
            }
        });

        uploadButton.addActionListener(e -> {
            if (titleField.getText().isBlank() || selectedFile[0] == null || uploadSubjectComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please fill all fields and select a file.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            handleUpload(titleField.getText(), (String) uploadSubjectComboBox.getSelectedItem(), selectedFile[0]);
        });

        return panel;
    }

    private void loadAndCacheSubjects() {
        try {
            allSubjects = subjectDAO.getAllSubjects();
            subjectNameToIdMap.clear();
            allSubjects.forEach(subject -> subjectNameToIdMap.put(subject.getName(), subject.getId()));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load subjects: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            allSubjects = new ArrayList<>();
        }
    }
    
    private void populateSubjectFilter(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        comboBox.addItem("All Subjects");
        allSubjects.stream().map(Subject::getName).distinct().sorted().forEach(comboBox::addItem);
    }

    private void loadMaterials(String titleFilter, String subjectFilter) {
        tableModel.setRowCount(0); // Clear existing data
        try {
            List<Material> materials = materialDAO.getMaterials(titleFilter, subjectFilter);
            for (Material material : materials) {
                // Add a hidden column for the ID
                Object[] rowData = {material.getTitle(), material.getSubjectName(), String.format("%.1f", material.getAverageRating()), material.getUploaderName(), material.getId()};
                
                // We need a way to add the ID without displaying it. A custom table model is one way.
                // For simplicity here, we'll just have to query it again on click.
                 tableModel.addRow(new Object[]{material.getTitle(), material.getSubjectName(), String.format("%.1f", material.getAverageRating()), material.getUploaderName()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load materials: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openMaterial(Material material) {
        try {
            if (Desktop.isDesktopSupported() && material.getFilePath() != null) {
                 Desktop.getDesktop().browse(new java.net.URI(material.getFilePath()));
            } else {
                 JOptionPane.showMessageDialog(this, "Cannot open link on this platform.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not open material: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpload(String title, String subjectName, File file) {
        try {
            // 1. Upload the file to Cloudinary
            String url = cloudinaryService.uploadFile(file, "note0/materials", null);
            if (url == null || url.isBlank()) {
                throw new Exception("Cloudinary did not return a URL.");
            }
            
            // 2. Get the subject ID from the cached map
            Long subjectId = subjectNameToIdMap.get(subjectName);
            if (subjectId == null) {
                throw new Exception("Could not find ID for subject: " + subjectName);
            }
            
            // 3. Add the material metadata to the database
            materialDAO.addMaterial(title, url, subjectId, loggedInUser.getId());
            
            JOptionPane.showMessageDialog(this, "Upload successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh the materials list in the browse tab
            loadMaterials("", "All Subjects"); 

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Upload failed: " + e.getMessage(), "Upload Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
