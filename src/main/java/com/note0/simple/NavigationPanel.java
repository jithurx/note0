package com.note0.simple;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;

/**
 * A unified navigation panel that provides access to all features of the application.
 */
public class NavigationPanel extends JPanel {
    private final User currentUser;
    private final DashboardForm parentDashboard;

    public NavigationPanel(User user, DashboardForm dashboard) {
        this.currentUser = user;
        this.parentDashboard = dashboard;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder("Navigation")
        ));

        initializeComponents();
    }

    private void initializeComponents() {
        // User Information Section
        JPanel userInfoPanel = createSectionPanel("User Info");
        userInfoPanel.add(new JLabel("Welcome, " + currentUser.getFullName()));
        JLabel roleLabel = new JLabel("Role: " + currentUser.getRole());
        roleLabel.setForeground(new Color(100, 100, 100));
        userInfoPanel.add(roleLabel);
        
        // Main Features Section
        JPanel featuresPanel = createSectionPanel("Features");
        
        JButton profileButton = createNavButton("My Profile", e -> parentDashboard.showProfileDialog());
        JButton uploadButton = createNavButton("Upload Material", e -> parentDashboard.showUploadDialog());
        JButton searchButton = createNavButton("Search Materials", e -> parentDashboard.showSearchDialog());
        JButton viewAllButton = createNavButton("View All Materials", e -> parentDashboard.loadMaterialsIntoTable());
        
        featuresPanel.add(profileButton);
        featuresPanel.add(uploadButton);
        featuresPanel.add(searchButton);
        featuresPanel.add(viewAllButton);

        // Admin Section (only visible for admin users)
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            JPanel adminPanel = createSectionPanel("Admin Tools");
            JButton adminButton = createNavButton("Admin Panel", e -> 
                new AdminForm(currentUser, parentDashboard.getSubjectDAO()).setVisible(true));
            adminPanel.add(adminButton);
            add(adminPanel);
        }

        // Add all sections to the navigation panel
        add(userInfoPanel);
        add(Box.createVerticalStrut(10));
        add(featuresPanel);

        // Statistics Section
        JPanel statsPanel = createSectionPanel("Statistics");
        statsPanel.add(new JLabel("Total Materials: " + countTotalMaterials()));
        add(Box.createVerticalStrut(10));
        add(statsPanel);
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(title),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JButton createNavButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 30));
        button.addActionListener(listener);
        return button;
    }

    private int countTotalMaterials() {
        try {
            return parentDashboard.getMaterialDAO().getMaterials("", "All Branches", 0, "All Subjects").size();
        } catch (Exception e) {
            return 0;
        }
    }
}