package com.note0.simple;

import javax.swing.*;
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
    private DefaultTableModel tableModel;
    private JTable materialTable;

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

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Build UI Panels ---
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        // --- Initial Data Load ---
        loadAndCacheSubjects();
        populateFilterComboBoxes();
        loadMaterialsIntoTable();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.add(new JLabel("Welcome, " + loggedInUser.getFullName() + "!"));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.showLoginPanel());
        welcomePanel.add(logoutButton);

        if ("ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            JButton adminButton = new JButton("Admin Panel");
            adminButton.addActionListener(e -> {
                AdminPanel adminPanel = new AdminPanel(this, subjectDAO);
                JFrame adminFrame = new JFrame("Admin - Subject Management");
                adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                adminFrame.getContentPane().add(adminPanel);
                adminFrame.pack();
                adminFrame.setLocationRelativeTo(this);
                adminFrame.setVisible(true);
            });
            welcomePanel.add(adminButton);
        }
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Branch:"));
        filterPanel.add(branchFilterComboBox);
        filterPanel.add(new JLabel("Semester:"));
        filterPanel.add(semesterFilterComboBox);
        filterPanel.add(new JLabel("Subject:"));
        filterPanel.add(subjectFilterComboBox);
        filterPanel.add(filterButton);
        
        topPanel.add(welcomePanel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);
        return topPanel;
    }

    private JScrollPane createCenterPanel() {
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Subject", "Uploader", "Avg Rating"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        materialTable = new JTable(tableModel);
        materialTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        materialTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) handleOpenFile();
            }
        });
        
        return new JScrollPane(materialTable);
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; bottomPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; bottomPanel.add(titleField, gbc);
        gbc.weightx = 0;

        gbc.gridx = 2; gbc.gridy = 0; bottomPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; bottomPanel.add(uploadSubjectComboBox, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 1; bottomPanel.add(chooseFileButton, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3; bottomPanel.add(selectedFileLabel, gbc);
        gbc.gridwidth = 1;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton rateButton = new JButton("Rate Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton uploadButton = new JButton("Upload New Material");
        buttonPanel.add(rateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(uploadButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; bottomPanel.add(buttonPanel, gbc);

        // Action Listeners
        filterButton.addActionListener(e -> loadMaterialsIntoTable());
        chooseFileButton.addActionListener(e -> handleChooseFile());
        uploadButton.addActionListener(e -> handleUpload());
        rateButton.addActionListener(e -> handleRate());
        deleteButton.addActionListener(e -> handleDelete());
        
        branchFilterComboBox.addActionListener(e -> populateFilterComboBoxes());
        semesterFilterComboBox.addActionListener(e -> populateFilterComboBoxes());

        return bottomPanel;
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
     * of the subject lists and the main materials table.
     */
    public void refreshData() {
        System.out.println("Refreshing dashboard data...");
        loadAndCacheSubjects();
        populateFilterComboBoxes();
        loadMaterialsIntoTable();
    }

    private void populateFilterComboBoxes() {
        // Temporarily disable listeners to prevent chain reactions
        for (var listener : branchFilterComboBox.getActionListeners()) branchFilterComboBox.removeActionListener(listener);
        for (var listener : semesterFilterComboBox.getActionListeners()) semesterFilterComboBox.removeActionListener(listener);

        // Populate Branch Filter (only if it's empty)
        if (branchFilterComboBox.getItemCount() == 0) {
            branchFilterComboBox.addItem("All Branches");
            allSubjects.stream().map(Subject::getBranch).distinct().sorted().forEach(branchFilterComboBox::addItem);
        }
        
        // Populate Semester Filter (only if it's empty)
        if (semesterFilterComboBox.getItemCount() == 0) {
            semesterFilterComboBox.addItem(0); // Represents "All Semesters"
            allSubjects.stream().map(Subject::getSemester).distinct().sorted().forEach(semesterFilterComboBox::addItem);
        }

        String selectedBranch = (String) branchFilterComboBox.getSelectedItem();
        Integer selectedSemester = (Integer) semesterFilterComboBox.getSelectedItem();
        
        Vector<String> filteredSubjects = new Vector<>();
        filteredSubjects.add("All Subjects");
        
        allSubjects.stream()
            .filter(s -> selectedBranch.equals("All Branches") || s.getBranch().equals(selectedBranch))
            .filter(s -> selectedSemester == 0 || s.getSemester() == selectedSemester)
            .map(Subject::getName)
            .sorted()
            .forEach(filteredSubjects::add);
        
        subjectFilterComboBox.setModel(new DefaultComboBoxModel<>(filteredSubjects));
        uploadSubjectComboBox.setModel(new DefaultComboBoxModel<>(filteredSubjects));
        if (uploadSubjectComboBox.getItemCount() > 0 && uploadSubjectComboBox.getItemAt(0).equals("All Subjects")) {
             uploadSubjectComboBox.removeItemAt(0);
        }

        // Re-enable listeners
        branchFilterComboBox.addActionListener(e -> populateFilterComboBoxes());
        semesterFilterComboBox.addActionListener(e -> populateFilterComboBoxes());
    }

    private void loadMaterialsIntoTable() {
        tableModel.setRowCount(0);
        try {
            String titleFilter = searchField.getText().trim();
            String branchFilter = branchFilterComboBox.getSelectedItem() != null ? (String) branchFilterComboBox.getSelectedItem() : "All Branches";
            Object semesterObj = semesterFilterComboBox.getSelectedItem();
            int semesterFilter = (semesterObj instanceof Integer) ? (Integer) semesterObj : 0;
            String subjectFilter = subjectFilterComboBox.getSelectedItem() != null ? (String) subjectFilterComboBox.getSelectedItem() : "All Subjects";

            List<Material> materials = materialDAO.getMaterials(titleFilter, branchFilter, semesterFilter, subjectFilter);
            for (Material material : materials) {
                tableModel.addRow(new Object[]{
                    material.getId(), material.getTitle(), material.getSubjectName(),
                    material.getUploaderName(), String.format("%.2f", material.getAverageRating())
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load materials: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
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
            loadMaterialsIntoTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Upload Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRate() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a material from the list to rate.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        long materialId = (long) tableModel.getValueAt(selectedRow, 0);
        String[] options = {"1", "2", "3", "4", "5"};
        String ratingStr = (String) JOptionPane.showInputDialog(
            this, "Select a rating:", "Rate Material",
            JOptionPane.QUESTION_MESSAGE, null, options, options[4]);

        if (ratingStr != null) {
            try {
                int score = Integer.parseInt(ratingStr);
                materialDAO.rateMaterial(materialId, loggedInUser.getId(), score);
                JOptionPane.showMessageDialog(this, "Rating saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMaterialsIntoTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to save rating: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDelete() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a material from the list to delete.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        long materialId = (long) tableModel.getValueAt(selectedRow, 0);
        String materialTitle = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Are you sure you want to delete '" + materialTitle + "'?\n\nThis will remove the material and all its ratings from the database.\nNote: The file will remain on Cloudinary.", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                materialDAO.deleteMaterial(materialId);
                JOptionPane.showMessageDialog(this, "Material deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMaterialsIntoTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to delete material: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleOpenFile() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow >= 0) {
            long materialId = (long) tableModel.getValueAt(selectedRow, 0);
            try {
                Material material = materialDAO.getMaterialById(materialId);
                if (material != null) {
                    String path = material.getFilePath();
                    if (path != null && (path.startsWith("http://") || path.startsWith("https://"))) {
                        try {
                            String lower = path.toLowerCase();
                            String suffix = lower.endsWith(".pdf") ? ".pdf" : lower.endsWith(".docx") ? ".docx" : lower.endsWith(".doc") ? ".doc" : lower.endsWith(".pptx") ? ".pptx" : lower.endsWith(".ppt") ? ".ppt" : lower.endsWith(".xlsx") ? ".xlsx" : lower.endsWith(".xls") ? ".xls" : "";
                            java.net.URL url = new java.net.URL(path);
                            java.nio.file.Path tmp = java.nio.file.Files.createTempFile("note0_", suffix);
                            try (java.io.InputStream in = url.openStream()) {
                                java.nio.file.Files.copy(in, tmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            }
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().open(tmp.toFile());
                            } else {
                                JOptionPane.showMessageDialog(this, "Downloaded to: " + tmp.toString(), "Downloaded", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception dlex) {
                            JOptionPane.showMessageDialog(this, "Could not download/open file: " + dlex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        File fileToOpen = new File(path);
                        if (fileToOpen.exists() && Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(fileToOpen);
                        } else {
                            JOptionPane.showMessageDialog(this, "File not found at path: " + path, "File Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not open file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}