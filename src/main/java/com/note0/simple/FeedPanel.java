package com.note0.simple;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * The main feed panel that serves as the home page of the application.
 * Features three sections: Recent Materials, Recommended For You, and Popular This Week.
 * Uses asymmetric card layouts with neobrutalist styling.
 */
public class FeedPanel extends JPanel {
    
    private final MainFrame mainFrame;
    private final User loggedInUser;
    private final MaterialDAO materialDAO;
    private final SubjectDAO subjectDAO;
    private final CloudinaryService cloudinaryService;
    
    // UI Components
    private NavigationBar navigationBar;
    private JScrollPane scrollPane;
    private JPanel contentPanel;
    
    // Section panels
    private JPanel recentSection;
    private JPanel recommendedSection;
    private JPanel popularSection;
    
    // Section content panels
    private JPanel recentCardsPanel;
    private JPanel recommendedCardsPanel;
    private JPanel popularCardsPanel;
    
    // Data
    private List<Material> recentMaterials;
    private List<Material> recommendedMaterials;
    private List<Material> popularMaterials;
    
    public FeedPanel(MainFrame mainFrame, User user, MaterialDAO materialDAO, SubjectDAO subjectDAO, CloudinaryService cloudinaryService) {
        this.mainFrame = mainFrame;
        this.loggedInUser = user;
        this.materialDAO = materialDAO;
        this.subjectDAO = subjectDAO;
        this.cloudinaryService = cloudinaryService;
        
        setupFeedPanel();
        loadFeedData();
        buildUI();
    }
    
    private void setupFeedPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(NeoBrutalLookAndFeel.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }
    
    private void loadFeedData() {
        try {
            // Load recent materials (newest 6)
            recentMaterials = materialDAO.getRecentMaterials(6);
            
            // Load recommended materials based on user's branch/semester (if available)
            // For now, we'll use general top-rated materials as recommendations
            recommendedMaterials = materialDAO.getTopRatedMaterials(6);
            
            // Load popular materials (highest rated)
            popularMaterials = materialDAO.getTopRatedMaterials(6);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading feed data: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void buildUI() {
        // Create navigation bar
        navigationBar = NavigationBar.createFeed(e -> handleNavigation(e));
        navigationBar.setActiveButton("HOME");
        
        // Create main content panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(NeoBrutalLookAndFeel.COLOR_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create sections
        createHeaderSection();
        createRecentSection();
        createRecommendedSection();
        createPopularSection();
        
        // Add sections to content panel
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(recentSection);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(recommendedSection);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(popularSection);
        contentPanel.add(Box.createVerticalGlue());
        
        // Create scroll pane
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(NeoBrutalLookAndFeel.COLOR_BG);
        
        // Add components to main panel
        add(navigationBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createHeaderSection() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Note0 Feed!");
        welcomeLabel.setFont(NeoBrutalLookAndFeel.FONT_ANTON.deriveFont(36f));
        welcomeLabel.setForeground(NeoBrutalLookAndFeel.COLOR_PRIMARY);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Discover, Share, and Learn Together");
        subtitleLabel.setFont(NeoBrutalLookAndFeel.FONT_INSTRUMENT_SERIF.deriveFont(18f));
        subtitleLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Add decorative elements
        JPanel decorations = new JPanel(new FlowLayout());
        decorations.setOpaque(false);
        
        decorations.add(NeoBrutalLookAndFeel.createDecorativeElement(
            NeoBrutalLookAndFeel.ShapeType.CIRCLE, 
            NeoBrutalLookAndFeel.COLOR_ACCENT1, 20));
        decorations.add(Box.createHorizontalStrut(10));
        decorations.add(NeoBrutalLookAndFeel.createDecorativeElement(
            NeoBrutalLookAndFeel.ShapeType.SQUARE, 
            NeoBrutalLookAndFeel.COLOR_ACCENT2, 16));
        decorations.add(Box.createHorizontalStrut(10));
        decorations.add(NeoBrutalLookAndFeel.createDecorativeElement(
            NeoBrutalLookAndFeel.ShapeType.CIRCLE, 
            NeoBrutalLookAndFeel.COLOR_SECONDARY, 18));
        
        headerPanel.add(decorations, BorderLayout.SOUTH);
        
        contentPanel.add(headerPanel);
    }
    
    private void createRecentSection() {
        recentSection = createFeedSection(
            "📚 Recent Materials", 
            "Latest uploads from the community",
            recentMaterials,
            recentCardsPanel = new JPanel()
        );
    }
    
    private void createRecommendedSection() {
        recommendedSection = createFeedSection(
            "⭐ Recommended For You", 
            "Materials we think you'll love",
            recommendedMaterials,
            recommendedCardsPanel = new JPanel()
        );
    }
    
    private void createPopularSection() {
        popularSection = createFeedSection(
            "🔥 Popular This Week", 
            "Highest rated materials",
            popularMaterials,
            popularCardsPanel = new JPanel()
        );
    }
    
    private JPanel createFeedSection(String title, String subtitle, List<Material> materials, JPanel cardsPanel) {
        // Main section panel
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
        sectionPanel.setBorder(BorderFactory.createTitledBorder(
            NeoBrutalLookAndFeel.BORDER_DASHED,
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            NeoBrutalLookAndFeel.FONT_ANTON.deriveFont(24f),
            NeoBrutalLookAndFeel.COLOR_PRIMARY
        ));
        sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS.deriveFont(14f));
        subtitleLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sectionPanel.add(subtitleLabel);
        
        // Cards panel with asymmetric layout
        cardsPanel.setLayout(new GridBagLayout());
        cardsPanel.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        // Add material cards with asymmetric positioning
        for (int i = 0; i < Math.min(materials.size(), 6); i++) {
            Material material = materials.get(i);
            
            // Create material card
            MaterialCard card = new MaterialCard(
                material, 
                MaterialCard.DisplayMode.COMPACT,
                this::handleMaterialClick
            );
            
            // Asymmetric positioning
            gbc.gridx = i % 3; // 3 columns
            gbc.gridy = i / 3; // 2 rows max
            
            // Add some randomness to positioning
            if (i % 2 == 0) {
                gbc.insets = new Insets(5, 5, 5, 15); // Slight offset for asymmetry
            } else {
                gbc.insets = new Insets(10, 10, 5, 5);
            }
            
            cardsPanel.add(card, gbc);
        }
        
        // If no materials, show placeholder
        if (materials.isEmpty()) {
            JLabel placeholderLabel = new JLabel("No materials available");
            placeholderLabel.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS.deriveFont(14f));
            placeholderLabel.setForeground(NeoBrutalLookAndFeel.COLOR_BORDER);
            placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
            cardsPanel.add(placeholderLabel, gbc);
        }
        
        sectionPanel.add(cardsPanel);
        
        return sectionPanel;
    }
    
    private void handleMaterialClick(Material material) {
        // Open the material (same logic as DashboardPanel)
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
    
    private void handleNavigation(ActionEvent e) {
        String command = e.getActionCommand();
        
        switch (command) {
            case "HOME":
                // Already on home, do nothing
                break;
            case "BROWSE":
                // Navigate to dashboard for full browsing
                mainFrame.showDashboardPanel(loggedInUser);
                break;
            case "UPLOAD":
                // Navigate to dashboard for uploading
                mainFrame.showDashboardPanel(loggedInUser);
                break;
            case "LOGOUT":
                mainFrame.showLoginPanel();
                break;
        }
    }
    
    /**
     * Refreshes the feed data and updates the UI
     */
    public void refreshFeed() {
        loadFeedData();
        
        // Clear existing cards
        recentCardsPanel.removeAll();
        recommendedCardsPanel.removeAll();
        popularCardsPanel.removeAll();
        
        // Rebuild sections
        createRecentSection();
        createRecommendedSection();
        createPopularSection();
        
        revalidate();
        repaint();
    }
}
