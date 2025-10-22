package com.note0.simple;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class FeedPanel extends JPanel {

    private final MainFrame mainFrame;
    private final User loggedInUser;
    private final MaterialDAO materialDAO;

    public FeedPanel(MainFrame mainFrame, User user, MaterialDAO materialDAO, SubjectDAO subjectDAO, CloudinaryService cloudinaryService) {
        this.mainFrame = mainFrame;
        this.loggedInUser = user;
        this.materialDAO = materialDAO;

        setLayout(new BorderLayout());

        // Navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton browseButton = new JButton("Browse All");
        browseButton.addActionListener(e -> mainFrame.showDashboardPanel(loggedInUser));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.showLoginPanel());
        navPanel.add(new JLabel("Welcome, " + loggedInUser.getFullName()));
        navPanel.add(browseButton);
        navPanel.add(logoutButton);
        add(navPanel, BorderLayout.NORTH);

        // Main Content
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Recent", createFeedSection("Recent Materials"));
        tabbedPane.addTab("Recommended", createFeedSection("Recommended Materials"));
        tabbedPane.addTab("Popular", createFeedSection("Popular Materials"));

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JScrollPane createFeedSection(String title) {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        
        List<Material> materials;
        try {
            if (title.equals("Recent Materials")) {
                materials = materialDAO.getRecentMaterials(10);
            } else { // For Recommended and Popular, we'll just get top rated for now
                materials = materialDAO.getTopRatedMaterials(10);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sectionPanel.add(new JLabel("Error loading materials."));
            return new JScrollPane(sectionPanel);
        }

        if (materials.isEmpty()) {
            sectionPanel.add(new JLabel("No materials to display."));
        } else {
            for (Material material : materials) {
                sectionPanel.add(createSimpleMaterialPanel(material));
                sectionPanel.add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(sectionPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return scrollPane;
    }

    private JPanel createSimpleMaterialPanel(Material material) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(new JLabel("Title: " + material.getTitle()));
        infoPanel.add(new JLabel("Subject: " + material.getSubjectName())); // Simplified
        infoPanel.add(new JLabel("Rating: " + String.format("%.1f", material.getAverageRating())));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton viewButton = new JButton("View");
        JButton rateButton = new JButton("Rate");
        
        viewButton.addActionListener(e -> handleMaterialClick(material));
        rateButton.addActionListener(e -> rateMaterial(material));

        buttonPanel.add(viewButton);
        buttonPanel.add(rateButton);

        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.EAST);
        return panel;
    }

    private void handleMaterialClick(Material material) {
        try {
            String path = material.getFilePath();
            if (path != null && (path.startsWith("http://") || path.startsWith("https://"))) {
                // Handle remote files
                try {
                    Desktop.getDesktop().browse(new java.net.URI(path));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Could not open link: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Handle local files
                java.io.File fileToOpen = new java.io.File(path);
                if (fileToOpen.exists() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(fileToOpen);
                } else {
                    JOptionPane.showMessageDialog(this, "File not found at path: " + path, "File Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not open file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void rateMaterial(Material material) {
        try {
            long materialId = material.getId();
            
            // Get current user rating
            int currentRating = materialDAO.getUserRating(materialId, loggedInUser.getId());
            
            // Show rating dialog
            String[] options = {"1 Star", "2 Stars", "3 Stars", "4 Stars", "5 Stars"};
            String message = "Rate: " + material.getTitle() + 
                           "\nCurrent rating: " + (currentRating > 0 ? currentRating + " stars" : "Not rated");
            
            int choice = JOptionPane.showOptionDialog(this, message, "Rate Material", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            
            if (choice >= 0) {
                int rating = choice + 1;
                materialDAO.addOrUpdateRating(materialId, loggedInUser.getId(), rating);
                JOptionPane.showMessageDialog(this, "Rating saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh the feed to show updated ratings
                mainFrame.showFeedPanel(loggedInUser);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error rating material: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}