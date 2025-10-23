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
    private List<Material> currentMaterials = new ArrayList<>();

    public DashboardPanel(MainFrame mainFrame, User user, MaterialDAO materialDAO, SubjectDAO subjectDAO, CloudinaryService cloudinaryService) {
        this.mainFrame = mainFrame;
        this.loggedInUser = user;
        this.materialDAO = materialDAO;
        this.subjectDAO = subjectDAO;
        this.cloudinaryService = cloudinaryService;

        setLayout(new BorderLayout());
        setBackground(UITheme.APP_BACKGROUND); // Set main background

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.LABEL_FONT);
        tabbedPane.addTab("Browse Materials", createBrowsePanel());
        tabbedPane.addTab("Upload Material", createUploadPanel());
        tabbedPane.addTab("My Uploads", createMyUploadsPanel());

        add(tabbedPane, BorderLayout.CENTER);
        
        JButton backButton = new JButton("Back to Feed");
        UITheme.styleSecondaryButton(backButton);
        backButton.addActionListener(e -> mainFrame.showFeedPanel(loggedInUser));
        
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBackground(UITheme.APP_BACKGROUND);
        southPanel.add(backButton);
        add(southPanel, BorderLayout.SOUTH);

        loadAndCacheSubjects(); // Load subjects once
    }

    private JPanel createBrowsePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.APP_BACKGROUND);
        panel.setBorder(UITheme.APP_PADDING);
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(UITheme.APP_BACKGROUND);
        JTextField searchField = new JTextField(20);
        JComboBox<String> subjectFilterComboBox = new JComboBox<>();
        JButton filterButton = new JButton("Search / Refresh");
        UITheme.stylePrimaryButton(filterButton);

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

        // --- APPLY TABLE STYLES ---
        materialsTable.setFont(UITheme.TABLE_BODY_FONT);
        materialsTable.getTableHeader().setFont(UITheme.TABLE_HEADER_FONT);
        materialsTable.setRowHeight(UITheme.TABLE_ROW_HEIGHT);
        // -------------------------

        materialsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        materialsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && materialsTable.getSelectedRow() != -1) {
                    int modelRow = materialsTable.convertRowIndexToModel(materialsTable.getSelectedRow());
                    Material material = currentMaterials.get(modelRow);
                    openMaterial(material);
                }
            }
        });

        // Populate filters and initial data
        loadAndCacheSubjects();
        populateSubjectFilter(subjectFilterComboBox);
        loadMaterials(searchField.getText(), (String) subjectFilterComboBox.getSelectedItem());

        filterButton.addActionListener(e -> loadMaterials(searchField.getText(), (String) subjectFilterComboBox.getSelectedItem()));

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(UITheme.APP_BACKGROUND);
        JButton rateButton = new JButton("Rate Selected");
        UITheme.stylePrimaryButton(rateButton);
        JButton viewButton = new JButton("View Selected");
        UITheme.styleSecondaryButton(viewButton);
        
        rateButton.addActionListener(e -> rateSelectedMaterial());
        viewButton.addActionListener(e -> viewSelectedMaterial());
        
        actionPanel.add(rateButton);
        actionPanel.add(viewButton);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(materialsTable), BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createUploadPanel() {
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(UITheme.APP_BACKGROUND);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UITheme.CARD_BACKGROUND);
        panel.setBorder(UITheme.createShadowBorder());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Upload New Material");
        titleLabel.setFont(UITheme.HEADING_FONT);
        titleLabel.setForeground(UITheme.TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField titleField = new JTextField(20);
        JComboBox<String> uploadSubjectComboBox = new JComboBox<>();
        JButton chooseFileButton = new JButton("Choose File");
        UITheme.styleSecondaryButton(chooseFileButton);
        JLabel selectedFileLabel = new JLabel("No file selected.");
        selectedFileLabel.setForeground(UITheme.TEXT_COLOR);
        final File[] selectedFile = {null}; // Using an array to be final and mutable
        JButton uploadButton = new JButton("Upload");
        UITheme.stylePrimaryButton(uploadButton);

        populateSubjectFilter(uploadSubjectComboBox);
        uploadSubjectComboBox.removeItem("All Subjects"); // Can't upload to 'All'

        // Layout
        gbc.gridx = 0; gbc.gridy = 1; 
        JLabel titleFieldLabel = new JLabel("Title:");
        titleFieldLabel.setFont(UITheme.LABEL_FONT);
        titleFieldLabel.setForeground(UITheme.TEXT_COLOR);
        panel.add(titleFieldLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(titleField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setFont(UITheme.LABEL_FONT);
        subjectLabel.setForeground(UITheme.TEXT_COLOR);
        panel.add(subjectLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST;
        panel.add(uploadSubjectComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        panel.add(chooseFileButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST;
        panel.add(selectedFileLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(uploadButton, gbc);

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

        wrapperPanel.add(panel, new GridBagConstraints()); // Add card to wrapper
        return wrapperPanel; // Return wrapper
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
        currentMaterials.clear(); // Clear current materials list
        try {
            List<Material> materials = materialDAO.getMaterials(titleFilter, subjectFilter);
            currentMaterials.addAll(materials); // Store materials for later access
            for (Material material : materials) {
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
            
            JOptionPane.showMessageDialog(this, "Upload successful! Your material is pending approval.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh the materials list in the browse tab
            loadMaterials("", "All Subjects"); 

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Upload failed: " + e.getMessage(), "Upload Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void rateSelectedMaterial() {
        int selectedRow = materialsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a material to rate.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            Material material = currentMaterials.get(selectedRow);
            long materialId = material.getId();
            
            // Get current user rating
            int currentRating = materialDAO.getUserRating(materialId, loggedInUser.getId());
            
            // Show rating dialog
            String[] options = {"1 Star", "2 Stars", "3 Stars", "4 Stars", "5 Stars"};
            String message = "Rate: " + material.getTitle() + 
                           "\nCurrent rating: " + (currentRating > 0 ? currentRating + " stars" : "Not rated");
            
            int choice = JOptionPane.showOptionDialog(this, message, "Rate Material", 
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            
            if (choice >= 0) {
                int rating = choice + 1;
                materialDAO.addOrUpdateRating(materialId, loggedInUser.getId(), rating);
                JOptionPane.showMessageDialog(this, "Rating saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the materials list to show updated rating
                loadMaterials("", "All Subjects");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error rating material: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void viewSelectedMaterial() {
        int selectedRow = materialsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a material to view.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Material material = currentMaterials.get(selectedRow);
        openMaterial(material);
    }
    
    private JPanel createMyUploadsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.APP_BACKGROUND);
        panel.setBorder(UITheme.APP_PADDING);
        
        // My uploads table
        String[] columnNames = {"Title", "Subject", "Rating", "Status"};
        DefaultTableModel myUploadsModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable myUploadsTable = new JTable(myUploadsModel);

        // --- APPLY TABLE STYLES ---
        myUploadsTable.setFont(UITheme.TABLE_BODY_FONT);
        myUploadsTable.getTableHeader().setFont(UITheme.TABLE_HEADER_FONT);
        myUploadsTable.setRowHeight(UITheme.TABLE_ROW_HEIGHT);
        // -------------------------

        myUploadsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Action buttons for my uploads
        JPanel myUploadsActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        myUploadsActionPanel.setBackground(UITheme.APP_BACKGROUND);
        JButton deleteMyUploadButton = new JButton("Delete Selected");
        UITheme.styleDangerButton(deleteMyUploadButton);
        JButton refreshMyUploadsButton = new JButton("Refresh");
        UITheme.styleSecondaryButton(refreshMyUploadsButton);
        
        deleteMyUploadButton.addActionListener(e -> deleteMyUpload(myUploadsTable, myUploadsModel));
        refreshMyUploadsButton.addActionListener(e -> loadMyUploads(myUploadsModel));
        
        myUploadsActionPanel.add(deleteMyUploadButton);
        myUploadsActionPanel.add(refreshMyUploadsButton);
        
        panel.add(new JScrollPane(myUploadsTable), BorderLayout.CENTER);
        panel.add(myUploadsActionPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadMyUploads(myUploadsModel);
        
        return panel;
    }
    
    private void loadMyUploads(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Material> myMaterials = materialDAO.getMaterialsByUser(loggedInUser.getId());
            for (Material material : myMaterials) {
                model.addRow(new Object[]{
                    material.getTitle(), 
                    material.getSubjectName(), 
                    String.format("%.1f", material.getAverageRating()),
                    material.getApprovalStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load your uploads: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteMyUpload(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an upload to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            List<Material> myMaterials = materialDAO.getMaterialsByUser(loggedInUser.getId());
            if (selectedRow < myMaterials.size()) {
                Material material = myMaterials.get(selectedRow);
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete '" + material.getTitle() + "'?\n\n" +
                    "This will remove the material from the database.\n" +
                    "The uploaded file will remain in Cloudinary storage.", 
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    materialDAO.deleteMaterial(material.getId());
                    JOptionPane.showMessageDialog(this, "Upload deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMyUploads(model); // Refresh the list
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting upload: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

