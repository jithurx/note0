package com.note0.simple;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {

    // --- State and Services ---
    private final MainFrame mainFrame;
    private final User loggedInUser;
    private final MaterialDAO materialDAO;
    private final SubjectDAO subjectDAO;
    private final CloudinaryService cloudinaryService;
    private List<Subject> allSubjects; // Cache the list of all subjects

    // --- UI Components ---
    private NavigationBar navigationBar;
    private JScrollPane scrollPane;
    private JPanel materialsGridPanel;
    private JPanel filterPanel;
    private JPanel uploadPanel;

    // Filter Components
    private JTextField searchField = new JTextField(15);
    private JComboBox<String> branchFilterComboBox = new JComboBox<>();
    private JComboBox<Integer> semesterFilterComboBox = new JComboBox<>();
    private JComboBox<String> subjectFilterComboBox = new JComboBox<>();
    private JButton filterButton = new JButton("Search / Refresh");

    // Upload Components
    private JTextField titleField = new JTextField();
    private JComboBox<String> uploadSubjectComboBox = new JComboBox<>();
    private JButton chooseFileButton = new JButton("Choose File");
    private JLabel selectedFileLabel = new JLabel("No file selected.");
    private File selectedFile;
    private Map<String, Long> subjectNameToIdMap = new HashMap<>();

    // --- Cloud Storage ---
    // Files will be uploaded to Cloudinary; DB stores the returned secure URL

    public DashboardPanel(MainFrame mainFrame, User user, MaterialDAO materialDAO, SubjectDAO subjectDAO, CloudinaryService cloudinaryService) {
        this.mainFrame = mainFrame;
        this.loggedInUser = user;
        this.materialDAO = materialDAO;
        this.subjectDAO = subjectDAO;
        this.cloudinaryService = cloudinaryService;

        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setBackground(NeoBrutalLookAndFeel.COLOR_BG);

        setupDashboardPanel();
    }
    
    private void setupDashboardPanel() {
        // Style text fields
        searchField.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS);
        titleField.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS);

        // Style combo boxes
        Font comboFont = NeoBrutalLookAndFeel.FONT_GENERAL_SANS.deriveFont(14f);
        branchFilterComboBox.setFont(comboFont);
        semesterFilterComboBox.setFont(comboFont);
        subjectFilterComboBox.setFont(comboFont);
        uploadSubjectComboBox.setFont(comboFont);

        // Style labels
        selectedFileLabel.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS);

        // Create navigation bar
        navigationBar = NavigationBar.createDashboard(e -> handleNavigation(e));
        navigationBar.setActiveButton("BROWSE");

        // Create main content area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(NeoBrutalLookAndFeel.COLOR_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create sections
        createFilterPanel();
        createMaterialsGridPanel();
        createUploadPanel();

        // Add sections to content panel
        contentPanel.add(filterPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(materialsGridPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(uploadPanel);

        // Create scroll pane
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(NeoBrutalLookAndFeel.COLOR_BG);

        // Add components to main panel
        add(navigationBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Initial Data Load ---
        loadAndCacheSubjects();
        populateFilterComboBoxes();
        loadMaterialsIntoGrid();
    }
    
    private void handleNavigation(java.awt.event.ActionEvent e) {
        String command = e.getActionCommand();
        
        switch (command) {
            case "HOME":
                // Navigate back to feed
                mainFrame.showFeedPanel(loggedInUser);
                break;
            case "BROWSE":
                // Already on browse, do nothing
                break;
            case "UPLOAD":
                // Scroll to upload section or focus on it
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                break;
            case "LOGOUT":
                mainFrame.showLoginPanel();
                break;
        }
    }
    
    private void createFilterPanel() {
        filterPanel = new JPanel();
        filterPanel.setLayout(new BorderLayout());
        filterPanel.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            NeoBrutalLookAndFeel.BORDER_DASHED,
            "🔍 Search & Filter",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            NeoBrutalLookAndFeel.FONT_ANTON.deriveFont(20f),
            NeoBrutalLookAndFeel.COLOR_PRIMARY
        ));
        filterPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Create filter controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlsPanel.setOpaque(false);

        // Style labels
        JLabel searchLabel = new JLabel("Search:");
        JLabel branchLabel = new JLabel("Branch:");
        JLabel semesterLabel = new JLabel("Semester:");
        JLabel subjectLabel = new JLabel("Subject:");

        searchLabel.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(14f));
        branchLabel.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(14f));
        semesterLabel.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(14f));
        subjectLabel.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(14f));

        controlsPanel.add(searchLabel);
        controlsPanel.add(searchField);
        controlsPanel.add(branchLabel);
        controlsPanel.add(branchFilterComboBox);
        controlsPanel.add(semesterLabel);
        controlsPanel.add(semesterFilterComboBox);
        controlsPanel.add(subjectLabel);
        controlsPanel.add(subjectFilterComboBox);
        controlsPanel.add(filterButton);

        filterPanel.add(controlsPanel, BorderLayout.CENTER);
        
        // Add action listeners
        filterButton.addActionListener(e -> loadMaterialsIntoGrid());
        branchFilterComboBox.addActionListener(e -> populateFilterComboBoxes());
        semesterFilterComboBox.addActionListener(e -> populateFilterComboBoxes());
    }
    
    private void createMaterialsGridPanel() {
        materialsGridPanel = new JPanel();
        materialsGridPanel.setLayout(new BorderLayout());
        materialsGridPanel.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
        materialsGridPanel.setBorder(BorderFactory.createTitledBorder(
            NeoBrutalLookAndFeel.BORDER_DASHED,
            "📚 All Materials",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            NeoBrutalLookAndFeel.FONT_ANTON.deriveFont(20f),
            NeoBrutalLookAndFeel.COLOR_SECONDARY
        ));

        // Grid will be populated in loadMaterialsIntoGrid()
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridBagLayout());
        gridPanel.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        materialsGridPanel.add(gridPanel, BorderLayout.CENTER);
    }
    
    private void createUploadPanel() {
        uploadPanel = new JPanel();
        uploadPanel.setLayout(new BorderLayout());
        uploadPanel.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
        uploadPanel.setBorder(BorderFactory.createTitledBorder(
            NeoBrutalLookAndFeel.BORDER_DASHED,
            "⬆️ Upload New Material",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            NeoBrutalLookAndFeel.FONT_ANTON.deriveFont(20f),
            NeoBrutalLookAndFeel.COLOR_ACCENT1
        ));
        uploadPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        // Create upload form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);
        gbc.weightx = 0;

        // Subject selection
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0;
        formPanel.add(uploadSubjectComboBox, gbc);
        gbc.weightx = 0;

        // File selection
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(chooseFileButton, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3;
        formPanel.add(selectedFileLabel, gbc);
        gbc.gridwidth = 1;

        // Upload button
        JButton uploadButton = createThemedButton("Upload New Material");
        uploadButton.addActionListener(e -> handleUpload());
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(uploadButton, gbc);

        uploadPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add action listeners
        chooseFileButton.addActionListener(e -> handleChooseFile());
    }


    /**
     * Creates a button with the Neo-Brutal theme styles
     */
    private JButton createThemedButton(String text) {
        JButton button = new JButton(text);
        styleButton(button);
        return button;
    }

    /**
     * Applies Neo-Brutal theme styles to an existing button
     */
    private void styleButton(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(NeoBrutalLookAndFeel.COLOR_ACCENT1);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(NeoBrutalLookAndFeel.COLOR_SECONDARY);
            }
        });
    }

    public void loadAndCacheSubjects() {
        try {
            this.allSubjects = subjectDAO.getAllSubjects();
            subjectNameToIdMap.clear();
            allSubjects.forEach(subject -> subjectNameToIdMap.put(subject.getName(), subject.getId()));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Fatal Error: Could not load subjects. " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            this.allSubjects = new ArrayList<>();
        }
    }

    /**
     * Public method to allow other components (like the AdminPanel) to trigger a refresh
     * of the subject lists and the main materials grid.
     */
    public void refreshData() {
        System.out.println("Refreshing dashboard data...");
        loadAndCacheSubjects();
        populateFilterComboBoxes();
        loadMaterialsIntoGrid();
    }

    private void populateFilterComboBoxes() {
        // Temporarily disable listeners to prevent chain reactions
        for (var listener : branchFilterComboBox.getActionListeners()) {
            branchFilterComboBox.removeActionListener(listener);
        }
        for (var listener : semesterFilterComboBox.getActionListeners()) {
            semesterFilterComboBox.removeActionListener(listener);
        }

        // Populate Branch Filter (only if it's empty)
        if (branchFilterComboBox.getItemCount() == 0) {
            branchFilterComboBox.addItem("All Branches");
            allSubjects.stream()
                .map(Subject::getBranch)
                .distinct()
                .sorted()
                .forEach(branchFilterComboBox::addItem);
        }
        
        // Populate Semester Filter (only if it's empty)
        if (semesterFilterComboBox.getItemCount() == 0) {
            semesterFilterComboBox.addItem(0); // Represents "All Semesters"
            allSubjects.stream()
                .map(Subject::getSemester)
                .distinct()
                .sorted()
                .forEach(semesterFilterComboBox::addItem);
        }

        String selectedBranch = (String) branchFilterComboBox.getSelectedItem();
        Integer selectedSemester = (Integer) semesterFilterComboBox.getSelectedItem();
        
        List<String> filteredSubjects = new ArrayList<>();
        filteredSubjects.add("All Subjects");
        
        allSubjects.stream()
            .filter(s -> selectedBranch.equals("All Branches") || s.getBranch().equals(selectedBranch))
            .filter(s -> selectedSemester == 0 || s.getSemester() == selectedSemester)
            .map(Subject::getName)
            .sorted()
            .forEach(filteredSubjects::add);
        
        DefaultComboBoxModel<String> filterModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> uploadModel = new DefaultComboBoxModel<>();
        
        filteredSubjects.forEach(filterModel::addElement);
        filteredSubjects.stream()
            .filter(s -> !s.equals("All Subjects"))
            .forEach(uploadModel::addElement);
            
        subjectFilterComboBox.setModel(filterModel);
        uploadSubjectComboBox.setModel(uploadModel);

        // Re-enable listeners
        branchFilterComboBox.addActionListener(e -> populateFilterComboBoxes());
        semesterFilterComboBox.addActionListener(e -> populateFilterComboBoxes());
    }

    private void loadMaterialsIntoGrid() {
        // Clear existing cards
        JPanel gridPanel = (JPanel) ((BorderLayout) materialsGridPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        gridPanel.removeAll();
        
        try {
            String titleFilter = searchField.getText().trim();
            String branchFilter = branchFilterComboBox.getSelectedItem() != null ? (String) branchFilterComboBox.getSelectedItem() : "All Branches";
            Object semesterObj = semesterFilterComboBox.getSelectedItem();
            int semesterFilter = (semesterObj instanceof Integer) ? (Integer) semesterObj : 0;
            String subjectFilter = subjectFilterComboBox.getSelectedItem() != null ? (String) subjectFilterComboBox.getSelectedItem() : "All Subjects";

            List<Material> materials = materialDAO.getMaterials(titleFilter, branchFilter, semesterFilter, subjectFilter);
            
            // Create grid layout
            GridBagLayout gridLayout = new GridBagLayout();
            gridPanel.setLayout(gridLayout);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.NORTHWEST;
            
            // Add material cards in grid
            for (int i = 0; i < materials.size(); i++) {
                Material material = materials.get(i);
                
                // Create material card
                MaterialCard card = new MaterialCard(
                    material, 
                    MaterialCard.DisplayMode.FULL,
                    this::handleMaterialClick
                );
                
                // Position in grid (3 columns)
                gbc.gridx = i % 3;
                gbc.gridy = i / 3;
                
                gridPanel.add(card, gbc);
            }
            
            // If no materials, show placeholder
            if (materials.isEmpty()) {
                JLabel placeholderLabel = new JLabel("No materials found matching your criteria");
                placeholderLabel.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS.deriveFont(16f));
                placeholderLabel.setForeground(NeoBrutalLookAndFeel.COLOR_BORDER);
                placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
                gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
                gridPanel.add(placeholderLabel, gbc);
            }
            
            gridPanel.revalidate();
            gridPanel.repaint();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load materials: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleMaterialClick(Material material) {
        // Open the material (same logic as FeedPanel)
        try {
            String path = material.getFilePath();
            if (path != null && (path.startsWith("http://") || path.startsWith("https://"))) {
                try {
                    String lower = path.toLowerCase();
                    String suffix = lower.endsWith(".pdf") ? ".pdf" : 
                                   lower.endsWith(".docx") ? ".docx" : 
                                   lower.endsWith(".doc") ? ".doc" : 
                                   lower.endsWith(".pptx") ? ".pptx" : 
                                   lower.endsWith(".ppt") ? ".ppt" : 
                                   lower.endsWith(".xlsx") ? ".xlsx" : 
                                   lower.endsWith(".xls") ? ".xls" : "";
                    java.net.URL url = new java.net.URL(path);
                    java.nio.file.Path tmp = java.nio.file.Files.createTempFile("note0_", suffix);
                    try (java.io.InputStream in = url.openStream()) {
                        java.nio.file.Files.copy(in, tmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(tmp.toFile());
                    } else {
                        JOptionPane.showMessageDialog(this, "Downloaded to: " + tmp.toString(), 
                            "Downloaded", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Could not download/open file: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                java.io.File fileToOpen = new java.io.File(path);
                if (fileToOpen.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(fileToOpen);
                } else {
                    JOptionPane.showMessageDialog(this, "File not found at path: " + path, 
                        "File Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not open file: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- Event Handler Methods ---

    private void handleChooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            selectedFileLabel.setText(selectedFile.getName());
        }
    }

    private void handleUpload() {
        if (titleField.getText().isBlank() || selectedFile == null || uploadSubjectComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Title, Subject, and a selected File are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String publicId = UUID.randomUUID().toString();
            String url = cloudinaryService.uploadFile(selectedFile, "note0/materials", publicId);
            String selectedSubjectName = (String) uploadSubjectComboBox.getSelectedItem();
            long subjectId = subjectNameToIdMap.get(selectedSubjectName);
            materialDAO.addMaterial(titleField.getText(), url, subjectId, loggedInUser.getId());
            JOptionPane.showMessageDialog(this, "Upload successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMaterialsIntoGrid();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Upload Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRate() {
        // This method is now handled through the material cards directly
        // Users can rate materials by clicking on them and using a context menu or dialog
        JOptionPane.showMessageDialog(this, "To rate a material, click on it and select 'Rate' from the options.", 
            "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDelete() {
        // This method is now handled through the material cards directly
        // Users can delete materials by clicking on them and using a context menu or dialog
        JOptionPane.showMessageDialog(this, "To delete a material, click on it and select 'Delete' from the options.", 
            "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}