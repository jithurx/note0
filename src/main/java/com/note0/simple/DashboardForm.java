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

    // UI Components
    private JScrollPane feedScrollPane;
    private final JTextField searchField = new JTextField(15);
    private final JComboBox<String> branchFilterComboBox = new JComboBox<>();
    private final JComboBox<Integer> semesterFilterComboBox = new JComboBox<>();
    private final JComboBox<String> filterSubjectComboBox = new JComboBox<>();
    private final JButton searchButton = new JButton("Search / Refresh");
    private final Map<String, Long> subjectNameToIdMap = new HashMap<>();

    // Upload directory
    private final String UPLOAD_DIRECTORY = "/home/jithu/Desktop/note0-uploads";

    public DashboardForm(User user, SubjectDAO subjectDAO, MaterialDAO materialDAO) {
        this.loggedInUser = user;
        this.subjectDAO = subjectDAO;
        this.materialDAO = materialDAO;

        initUI(); // Initialize all UI components and listeners
        loadSubjectsIntoComboBox(); // Load subjects into dropdowns
    }

    private void initUI() {
        setTitle("Note0 Dashboard - Welcome, " + loggedInUser.getFullName());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Create the center feed panel
        JPanel feedPanel = new JPanel();
        feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));
        feedScrollPane = new JScrollPane(feedPanel);
        feedScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Create the filter panel at the top
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // First line of filters
        gbc.gridx = 0; gbc.gridy = 0; filterPanel.add(new JLabel("Branch:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; filterPanel.add(branchFilterComboBox, gbc);
        gbc.gridx = 2; gbc.gridy = 0; filterPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; filterPanel.add(semesterFilterComboBox, gbc);

        // Second line of filters
        gbc.gridx = 0; gbc.gridy = 1; filterPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; filterPanel.add(filterSubjectComboBox, gbc);
        gbc.gridx = 2; gbc.gridy = 1; filterPanel.add(new JLabel("Search Title:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; filterPanel.add(searchField, gbc);
        
        // Add the search button, spanning across to the right
        gbc.gridx = 4; gbc.gridy = 0;
        gbc.gridheight = 2; // Make the button two rows tall
        gbc.fill = GridBagConstraints.VERTICAL; // Fill the vertical space
        filterPanel.add(searchButton, gbc);

        // Add the filter panel to the top panel
        topPanel.add(filterPanel, BorderLayout.EAST);

        // --- CENTER PANEL: The feed of materials ---
        JPanel initialFeedPanel = new JPanel();
        initialFeedPanel.setLayout(new BoxLayout(initialFeedPanel, BoxLayout.Y_AXIS));
        feedScrollPane = new JScrollPane(initialFeedPanel);
        feedScrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        // --- Create and add the navigation panel ---
        NavigationPanel navigationPanel = new NavigationPanel(loggedInUser, this);
        navigationPanel.setPreferredSize(new Dimension(250, 0));

        // --- Add all panels to the main window ---
        add(topPanel, BorderLayout.NORTH);
        add(feedScrollPane, BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.WEST);

        // --- Attach Event Listeners to UI Components ---
        searchButton.addActionListener(e -> loadMaterialsIntoTable());
    }

    /**
     * Fetches all subjects from the database and populates both the upload and filter combo boxes.
     */
    private void loadSubjectsIntoComboBox() {
        try {
            List<Subject> subjects = subjectDAO.getAllSubjects();
            filterSubjectComboBox.removeAllItems();
            subjectNameToIdMap.clear();

            filterSubjectComboBox.addItem("All Subjects"); // Add default filter option

            for (Subject subject : subjects) {
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
    // Getter methods for NavigationPanel
    public MaterialDAO getMaterialDAO() {
        return materialDAO;
    }

    public SubjectDAO getSubjectDAO() {
        return subjectDAO;
    }
    
    // Make these methods public so they can be called from NavigationPanel
    public void loadMaterialsIntoTable() {
        JPanel feedPanel = new JPanel();
        feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));

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
                NotePostPanel postPanel = new NotePostPanel(material, loggedInUser, materialDAO, new LikeDAO());
                feedPanel.add(postPanel);
                feedPanel.add(Box.createVerticalStrut(10)); // Add spacing between posts
            }

            // Replace the old table with the new feed
            JScrollPane scrollPane = (JScrollPane) getContentPane().getComponent(1); // Get the CENTER component
            scrollPane.setViewportView(feedPanel);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load materials: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows a dialog for uploading a new material.
     */
    public void showUploadDialog() {
        JDialog uploadDialog = new JDialog(this, "Upload Material", true);
        uploadDialog.setLayout(new BorderLayout(10, 10));
        uploadDialog.setSize(500, 300);
        uploadDialog.setLocationRelativeTo(this);

        JPanel uploadPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components
        gbc.gridx = 0; gbc.gridy = 0;
        uploadPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        JTextField dialogTitleField = new JTextField(20);
        uploadPanel.add(dialogTitleField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        uploadPanel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> dialogSubjectComboBox = new JComboBox<>();
        uploadPanel.add(dialogSubjectComboBox, gbc);

        // Load subjects
        try {
            List<Subject> subjects = subjectDAO.getAllSubjects();
            for (Subject subject : subjects) {
                dialogSubjectComboBox.addItem(subject.getName());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Could not load subjects: " + e.getMessage());
        }

        gbc.gridx = 0; gbc.gridy = 2;
        JButton dialogChooseButton = new JButton("Choose File");
        uploadPanel.add(dialogChooseButton, gbc);
        
        gbc.gridx = 1;
        JLabel fileNameLabel = new JLabel("No file selected");
        uploadPanel.add(fileNameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton dialogUploadButton = new JButton("Upload");
        uploadPanel.add(dialogUploadButton, gbc);

        final File[] dialogSelectedFile = new File[1];
        dialogChooseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(uploadDialog) == JFileChooser.APPROVE_OPTION) {
                dialogSelectedFile[0] = fileChooser.getSelectedFile();
                fileNameLabel.setText(dialogSelectedFile[0].getName());
            }
        });

        dialogUploadButton.addActionListener(e -> {
            if (dialogTitleField.getText().isBlank() || dialogSelectedFile[0] == null || dialogSubjectComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(uploadDialog, "Title, Subject, and a selected File are required.");
                return;
            }
            
            try {
                String newFileName = UUID.randomUUID().toString() + "_" + dialogSelectedFile[0].getName();
                Path targetPath = Paths.get(UPLOAD_DIRECTORY, newFileName);

                Files.createDirectories(targetPath.getParent());
                Files.copy(dialogSelectedFile[0].toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                String selectedSubjectName = (String) dialogSubjectComboBox.getSelectedItem();
                long subjectId = subjectNameToIdMap.get(selectedSubjectName);

                materialDAO.addMaterial(dialogTitleField.getText(), targetPath.toString(), subjectId, loggedInUser.getId());
                
                JOptionPane.showMessageDialog(uploadDialog, "Upload successful!");
                uploadDialog.dispose();
                loadMaterialsIntoTable(); // Refresh the list
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(uploadDialog, "Database error during upload: " + ex.getMessage());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(uploadDialog, "File system error during upload: " + ex.getMessage());
            }
        });

        uploadDialog.add(uploadPanel);
        uploadDialog.setVisible(true);
    }

    public void showSearchDialog() {
        JDialog searchDialog = new JDialog(this, "Search Materials", true);
        searchDialog.setLayout(new BorderLayout(10, 10));
        searchDialog.setSize(500, 200);
        searchDialog.setLocationRelativeTo(this);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title search
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        JTextField dialogSearchField = new JTextField(20);
        searchPanel.add(dialogSearchField, gbc);

        // Branch filter
        gbc.gridx = 0; gbc.gridy = 1;
        searchPanel.add(new JLabel("Branch:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> dialogBranchComboBox = new JComboBox<>(new String[]{"All Branches", "CSE", "ECE", "EEE", "MECH"});
        searchPanel.add(dialogBranchComboBox, gbc);

        // Semester filter
        gbc.gridx = 0; gbc.gridy = 2;
        searchPanel.add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> dialogSemesterComboBox = new JComboBox<>(new String[]{"All Semesters", "1", "2", "3", "4", "5", "6", "7", "8"});
        searchPanel.add(dialogSemesterComboBox, gbc);

        // Search button
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton dialogSearchButton = new JButton("Search");
        searchPanel.add(dialogSearchButton, gbc);

        dialogSearchButton.addActionListener(e -> {
            searchField.setText(dialogSearchField.getText());
            branchFilterComboBox.setSelectedItem(dialogBranchComboBox.getSelectedItem());
            semesterFilterComboBox.setSelectedItem(dialogSemesterComboBox.getSelectedItem());
            loadMaterialsIntoTable();
            searchDialog.dispose();
        });

        searchDialog.add(searchPanel);
        searchDialog.setVisible(true);
    }



    /**
     * Shows a dialog for the user to view and update their profile.
     */
    public void showProfileDialog() {
        JDialog profileDialog = new JDialog(this, "User Profile", true);
        profileDialog.setLayout(new BorderLayout(10, 10));
        profileDialog.setSize(400, 300);
        profileDialog.setLocationRelativeTo(this);

        // Profile information panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(loggedInUser.getFullName()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(loggedInUser.getEmail()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(loggedInUser.getRole()), gbc);

        // Password change panel
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Change Password"));

        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        JButton saveButton = new JButton("Save Password");

        gbc.gridx = 0; gbc.gridy = 0;
        passwordPanel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        passwordPanel.add(newPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        passwordPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        passwordPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        passwordPanel.add(saveButton, gbc);

        // Add panels to dialog
        profileDialog.add(infoPanel, BorderLayout.NORTH);
        profileDialog.add(passwordPanel, BorderLayout.CENTER);

        // Save button action
        saveButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(profileDialog, 
                    "Please enter both password fields.", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(profileDialog,
                    "Passwords do not match.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Use UserDAO to update the password
                UserDAO userDAO = new UserDAO();
                userDAO.updatePassword(loggedInUser.getId(), newPassword);
                
                JOptionPane.showMessageDialog(profileDialog,
                    "Password updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                profileDialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(profileDialog,
                    "Error updating password: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        profileDialog.setVisible(true);
    }


}