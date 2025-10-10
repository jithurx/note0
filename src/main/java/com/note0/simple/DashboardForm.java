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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The main application window displayed after a successful login.
 * This class serves as the central dashboard for all user interactions.
 */
public class DashboardForm extends JFrame {

    // Data and Logic
    private final User loggedInUser;
    private final MaterialDAO materialDAO;
    private final SubjectDAO subjectDAO;

    // UI Components for the main table
    private JTable materialTable;
    private DefaultTableModel tableModel;

    // UI Components for the Action Panel (Uploading/Rating)
    private final JTextField titleField = new JTextField();
    private final JComboBox<String> subjectComboBox = new JComboBox<>();
    private final JButton chooseFileButton = new JButton("Choose File");
    private final JButton uploadButton = new JButton("Upload");
    private final JButton rateButton = new JButton("Rate Selected");
    private final JLabel selectedFileLabel = new JLabel("No file selected.");
    private File selectedFile;

    // UI Components for the Top Panel (Searching/Filtering)
    private final JTextField searchField = new JTextField(15);
    private final JComboBox<String> branchFilterComboBox = new JComboBox<>();
    private final JComboBox<Integer> semesterFilterComboBox = new JComboBox<>();
    private final JComboBox<String> filterSubjectComboBox = new JComboBox<>();
    private final JButton searchButton = new JButton("Search / Refresh");

    // A helper map to easily look up a subject's ID by its name
    private final Map<String, Long> subjectNameToIdMap = new HashMap<>();

    // --- IMPORTANT: MAKE SURE THIS FOLDER EXISTS ON YOUR COMPUTER ---
    private final String UPLOAD_DIRECTORY = "/home/jithu/Desktop/note0-uploads";
    // For Windows, change this path, e.g., "C:/Users/YourUser/Desktop/note0-uploads"
    // --------------------------------------------------------------------

    public DashboardForm(User user, SubjectDAO subjectDAO, MaterialDAO materialDAO) {
        this.loggedInUser = user;
        this.subjectDAO = subjectDAO;
        this.materialDAO = materialDAO;

        initUI(); // Initialize all UI components and listeners
        loadSubjectsIntoComboBox(); // Load subjects into dropdowns
        loadMaterialsIntoTable(); // Load initial material list
    }

    private void initUI() {
        setTitle("Note0 Dashboard - Welcome, " + loggedInUser.getFullName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- TOP PANEL: Contains Welcome message, Search, Filter, and Admin Button ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Welcome, " + loggedInUser.getFullName()));
        topPanel.add(new JSeparator(SwingConstants.VERTICAL));
        topPanel.add(new JLabel("Search Title:"));
        topPanel.add(searchField);
        topPanel.add(new JLabel("Filter by Subject:"));
        topPanel.add(filterSubjectComboBox);
        topPanel.add(searchButton);

        // Conditionally add the Admin Panel button only if the user's role is 'ADMIN'
        if ("ADMIN".equalsIgnoreCase(loggedInUser.getRole())) {
            JButton adminButton = new JButton("Admin Panel");
            adminButton.addActionListener(e -> new AdminForm(subjectDAO).setVisible(true));
            topPanel.add(adminButton);
        }

        // --- LEFT PANEL: Contains actions like uploading and rating ---
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; actionPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; actionPanel.add(titleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; actionPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; actionPanel.add(subjectComboBox, gbc);
        gbc.gridx = 0; gbc.gridy = 2; actionPanel.add(chooseFileButton, gbc);
        gbc.gridx = 1; gbc.gridy = 2; actionPanel.add(selectedFileLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 3; actionPanel.add(rateButton, gbc);
        gbc.gridx = 1; gbc.gridy = 3; actionPanel.add(uploadButton, gbc);

        // --- CENTER PANEL: The main table displaying materials ---
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Subject", "Uploader", "Avg Rating"}, 0);
        materialTable = new JTable(tableModel);

        // --- Add all panels to the main window ---
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(materialTable), BorderLayout.CENTER);
        add(actionPanel, BorderLayout.WEST);

        // --- Attach Event Listeners to UI Components ---
        searchButton.addActionListener(e -> loadMaterialsIntoTable());
        chooseFileButton.addActionListener(e -> handleChooseFile());
        uploadButton.addActionListener(e -> handleUpload());
        rateButton.addActionListener(e -> handleRate());
        materialTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) handleOpenFile();
            }
        });
    }

    /**
     * Fetches all subjects from the database and populates both the upload and filter combo boxes.
     */
    private void loadSubjectsIntoComboBox() {
        try {
            List<Subject> subjects = subjectDAO.getAllSubjects();
            subjectComboBox.removeAllItems();
            filterSubjectComboBox.removeAllItems();
            subjectNameToIdMap.clear();

            filterSubjectComboBox.addItem("All Subjects"); // Add default filter option

            for (Subject subject : subjects) {
                subjectComboBox.addItem(subject.getName());
                filterSubjectComboBox.addItem(subject.getName());
                subjectNameToIdMap.put(subject.getName(), subject.getId());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load subjects: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fetches materials from the database based on the current search/filter criteria and populates the table.
     */
    private void loadMaterialsIntoTable() {
        tableModel.setRowCount(0); // Clear the table first
        try {
            String titleFilter = searchField.getText().trim();
            String branchFilter = (String) branchFilterComboBox.getSelectedItem();
            Object semesterObj = semesterFilterComboBox.getSelectedItem();
            int semesterFilter = (semesterObj instanceof Integer) ? (Integer) semesterObj : 0;
            String subjectFilter = (String) filterSubjectComboBox.getSelectedItem();

            if (branchFilter == null) branchFilter = "All Branches";
            if (subjectFilter == null) subjectFilter = "All Subjects";

            List<Material> materials = materialDAO.getMaterials(titleFilter, branchFilter, semesterFilter, subjectFilter);

            for (Material material : materials) {
                tableModel.addRow(new Object[]{
                    material.getId(),
                    material.getTitle(),
                    material.getSubjectName(),
                    material.getUploaderName(),
                    String.format("%.2f", material.getAverageRating())
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load materials: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens a file chooser dialog to let the user select a file.
     */
    private void handleChooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            selectedFileLabel.setText(selectedFile.getName());
        }
    }

    /**
     * Handles the logic for uploading a new material.
     */
    private void handleUpload() {
        if (titleField.getText().isBlank() || selectedFile == null || subjectComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Title, Subject, and a selected File are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            String newFileName = UUID.randomUUID().toString() + "_" + selectedFile.getName();
            Path targetPath = Paths.get(UPLOAD_DIRECTORY, newFileName);

            Files.createDirectories(targetPath.getParent());
            Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String selectedSubjectName = (String) subjectComboBox.getSelectedItem();
            long subjectId = subjectNameToIdMap.get(selectedSubjectName);

            materialDAO.addMaterial(titleField.getText(), targetPath.toString(), subjectId, loggedInUser.getId());
            
            JOptionPane.showMessageDialog(this, "Upload successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMaterialsIntoTable(); // Refresh the list
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error while uploading: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "File system error while uploading: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles the logic for rating a selected material.
     */
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
                loadMaterialsIntoTable(); // Refresh table to show the new average
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save rating: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the logic for opening a file when a table row is double-clicked.
     */
    private void handleOpenFile() {
        int selectedRow = materialTable.getSelectedRow();
        if (selectedRow >= 0) {
            long materialId = (long) tableModel.getValueAt(selectedRow, 0);
            try {
                Material material = materialDAO.getMaterialById(materialId);
                if (material != null) {
                    File fileToOpen = new File(material.getFilePath());
                    if (fileToOpen.exists() && Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(fileToOpen);
                    } else {
                        JOptionPane.showMessageDialog(this, "File not found at path: " + material.getFilePath(), "File Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException | IOException ex) {
                String errorType = ex instanceof SQLException ? "Database error while fetching file" : "Error opening file";
                JOptionPane.showMessageDialog(this, errorType + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}