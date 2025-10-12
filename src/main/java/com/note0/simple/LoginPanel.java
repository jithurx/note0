package com.note0.simple;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * The panel for handling user login.
 * This class is responsible for the login UI and logic. Upon successful login,
 * it calls a method on the MainFrame to switch to the dashboard view.
 */
public class LoginPanel extends JPanel {

    // A reference to the main application frame to control navigation
    private final MainFrame mainFrame;
    private final UserDAO userDAO;

    // UI Components
    private JTextField emailField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton loginButton = new JButton("Login");
    private JButton registerNavButton = new JButton("Create New Account");
    private JLabel titleLabel = new JLabel("Welcome to Note0");

    public LoginPanel(MainFrame mainFrame, UserDAO userDAO) {
        this.mainFrame = mainFrame;
        this.userDAO = userDAO;
        
        setupLoginPanel();
    }
    
    private void setupLoginPanel() {
        // Apply the theme styles
        setBackground(NeoBrutalLookAndFeel.COLOR_BG);
        setLayout(new BorderLayout());
        
        // Create main content panel with asymmetric card and decorative elements
        JPanel contentPanel = createAsymmetricCardWithDecorations();
        add(contentPanel, BorderLayout.CENTER);
        
        // Setup action listeners
        setupActionListeners();
    }
    
    private JPanel createAsymmetricCard() {
        // Main card panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(500, 500));
        cardPanel.setMaximumSize(new Dimension(500, 500));
        
        // Apply asymmetric styling
        NeoBrutalLookAndFeel.styleAsAsymmetricCard(
            cardPanel, 
            NeoBrutalLookAndFeel.COLOR_WHITE,
            NeoBrutalLookAndFeel.COLOR_PRIMARY,
            2.0 // Slight rotation
        );
        
        // Create content area
        JPanel contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setOpaque(false);
        contentArea.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Title with multi-color styling
        titleLabel.setText("Welcome to Note0");
        titleLabel.setFont(NeoBrutalLookAndFeel.FONT_ANTON.deriveFont(42f));
        titleLabel.setForeground(NeoBrutalLookAndFeel.COLOR_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to continue");
        subtitleLabel.setFont(NeoBrutalLookAndFeel.FONT_INSTRUMENT_SERIF.deriveFont(16f));
        subtitleLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Form panel
        JPanel formPanel = createFormPanel();
        
        // Add components to content area
        contentArea.add(titleLabel);
        contentArea.add(subtitleLabel);
        contentArea.add(formPanel);
        contentArea.add(Box.createVerticalGlue());
        
        cardPanel.add(contentArea, BorderLayout.CENTER);
        
        return cardPanel;
    }
    
    private JPanel createAsymmetricCardWithDecorations() {
        // Create the main card
        JPanel cardPanel = createAsymmetricCard();
        
        // Create a container panel with overlay layout for decorations
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new OverlayLayout(containerPanel));
        containerPanel.setOpaque(false);
        
        // Add the main card
        containerPanel.add(cardPanel);
        
        // Add decorative elements
        JPanel topLeftDecor = NeoBrutalLookAndFeel.createDecorativeElement(
            NeoBrutalLookAndFeel.ShapeType.CIRCLE,
            NeoBrutalLookAndFeel.COLOR_ACCENT1,
            30
        );
        topLeftDecor.setPreferredSize(new Dimension(40, 40));
        topLeftDecor.setOpaque(false);
        topLeftDecor.setAlignmentX(0.1f);
        topLeftDecor.setAlignmentY(0.1f);
        
        JPanel topRightDecor = NeoBrutalLookAndFeel.createDecorativeElement(
            NeoBrutalLookAndFeel.ShapeType.SQUARE,
            NeoBrutalLookAndFeel.COLOR_ACCENT2,
            25
        );
        topRightDecor.setPreferredSize(new Dimension(35, 35));
        topRightDecor.setOpaque(false);
        topRightDecor.setAlignmentX(0.9f);
        topRightDecor.setAlignmentY(0.1f);
        
        containerPanel.add(topLeftDecor);
        containerPanel.add(topRightDecor);
        
        return containerPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        
        // Email field with styled label
        JLabel emailLabel = new JLabel("📧 Email:");
        emailLabel.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(16f));
        emailLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(emailLabel, gbc);
        
        // Style email field
        emailField.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS.deriveFont(14f));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NeoBrutalLookAndFeel.COLOR_PRIMARY, 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);
        
        // Password field with styled label
        JLabel passwordLabel = new JLabel("🔒 Password:");
        passwordLabel.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(16f));
        passwordLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(passwordLabel, gbc);
        
        // Style password field
        passwordField.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS.deriveFont(14f));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NeoBrutalLookAndFeel.COLOR_SECONDARY, 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);
        
        // Button panel
        JPanel buttonPanel = createStyledButtonPanel();
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 10, 0, 10);
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createStyledButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        // Style login button
        loginButton.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(18f));
        loginButton.setForeground(NeoBrutalLookAndFeel.COLOR_WHITE);
        loginButton.setBackground(NeoBrutalLookAndFeel.COLOR_SECONDARY);
        loginButton.setBorder(NeoBrutalLookAndFeel.BORDER_BUTTON_SHADOW);
        loginButton.setPreferredSize(new Dimension(120, 50));
        loginButton.setText("LOGIN");
        
        // Style register button
        registerNavButton.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(18f));
        registerNavButton.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        registerNavButton.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
        registerNavButton.setBorder(NeoBrutalLookAndFeel.BORDER_BUTTON_SHADOW);
        registerNavButton.setPreferredSize(new Dimension(160, 50));
        registerNavButton.setText("REGISTER");
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerNavButton);
        
        return buttonPanel;
    }
    
    
    private void setupActionListeners() {
        // Enhanced button hover effects
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(NeoBrutalLookAndFeel.COLOR_ACCENT1);
                loginButton.setBorder(NeoBrutalLookAndFeel.createColoredShadow(
                    NeoBrutalLookAndFeel.COLOR_ACCENT1, 6, 8));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(NeoBrutalLookAndFeel.COLOR_SECONDARY);
                loginButton.setBorder(NeoBrutalLookAndFeel.BORDER_BUTTON_SHADOW);
            }
        });

        registerNavButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerNavButton.setBackground(NeoBrutalLookAndFeel.COLOR_PRIMARY);
                registerNavButton.setForeground(NeoBrutalLookAndFeel.COLOR_WHITE);
                registerNavButton.setBorder(NeoBrutalLookAndFeel.createColoredShadow(
                    NeoBrutalLookAndFeel.COLOR_PRIMARY, 6, 8));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerNavButton.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
                registerNavButton.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
                registerNavButton.setBorder(NeoBrutalLookAndFeel.BORDER_BUTTON_SHADOW);
            }
        });

        // Action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerNavButton.addActionListener(e -> mainFrame.showRegistrationPanel());
    }

    /**
     * Handles the login button click event.
     */
    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isBlank() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User loggedInUser = userDAO.loginUser(email, password);

            if (loggedInUser != null) {
                // Login successful!
                // Tell the MainFrame to create and show the feed (home page) for this user.
                mainFrame.showFeedPanel(loggedInUser);
            } else {
                // Login failed
                JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "A database error occurred. Please try again later.", "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Also print the full error to the console for debugging
        }
    }
}