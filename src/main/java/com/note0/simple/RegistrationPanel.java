package com.note0.simple;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * The panel for handling new user registration.
 * It provides a form for new users and, upon successful registration,
 * navigates back to the login panel via the MainFrame.
 */
public class RegistrationPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserDAO userDAO;

    // UI Components
    private JTextField fullNameField = new JTextField(20);
    private JTextField emailField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JButton registerButton = new JButton("Register");
    private JButton backToLoginButton = new JButton("Back to Login");
    private JLabel titleLabel = new JLabel("Create Account");

    public RegistrationPanel(MainFrame mainFrame, UserDAO userDAO) {
        this.mainFrame = mainFrame;
        this.userDAO = userDAO;
        
        setupRegistrationPanel();
    }
    
    private void setupRegistrationPanel() {
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
        cardPanel.setPreferredSize(new Dimension(400, 450));
        cardPanel.setMaximumSize(new Dimension(400, 450));
        
        // Apply asymmetric styling with slight negative rotation
        NeoBrutalLookAndFeel.styleAsAsymmetricCard(
            cardPanel, 
            NeoBrutalLookAndFeel.COLOR_WHITE,
            NeoBrutalLookAndFeel.COLOR_SECONDARY,
            -1.5 // Slight negative rotation for asymmetry
        );
        
        // Create content area
        JPanel contentArea = new JPanel();
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));
        contentArea.setOpaque(false);
        contentArea.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Title with multi-color styling
        titleLabel.setText("Create Account");
        titleLabel.setFont(NeoBrutalLookAndFeel.FONT_ANTON.deriveFont(32f));
        titleLabel.setForeground(NeoBrutalLookAndFeel.COLOR_SECONDARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Join the community");
        subtitleLabel.setFont(NeoBrutalLookAndFeel.FONT_INSTRUMENT_SERIF.deriveFont(16f));
        subtitleLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
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
            NeoBrutalLookAndFeel.ShapeType.SQUARE,
            NeoBrutalLookAndFeel.COLOR_ACCENT2,
            30
        );
        topLeftDecor.setPreferredSize(new Dimension(35, 35));
        topLeftDecor.setOpaque(false);
        topLeftDecor.setAlignmentX(0.1f);
        topLeftDecor.setAlignmentY(0.1f);
        
        JPanel topRightDecor = NeoBrutalLookAndFeel.createDecorativeElement(
            NeoBrutalLookAndFeel.ShapeType.CIRCLE,
            NeoBrutalLookAndFeel.COLOR_ACCENT1,
            25
        );
        topRightDecor.setPreferredSize(new Dimension(30, 30));
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
        gbc.insets = new Insets(12, 10, 12, 10);
        
        // Full Name field with styled label
        JLabel fullNameLabel = new JLabel("👤 Full Name:");
        fullNameLabel.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(16f));
        fullNameLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(fullNameLabel, gbc);
        
        // Style full name field
        fullNameField.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS.deriveFont(14f));
        fullNameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NeoBrutalLookAndFeel.COLOR_SECONDARY, 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(fullNameField, gbc);
        
        // Email field with styled label
        JLabel emailLabel = new JLabel("📧 Email:");
        emailLabel.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(16f));
        emailLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(emailLabel, gbc);
        
        // Style email field
        emailField.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS.deriveFont(14f));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NeoBrutalLookAndFeel.COLOR_PRIMARY, 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);
        
        // Password field with styled label
        JLabel passwordLabel = new JLabel("🔒 Password:");
        passwordLabel.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(16f));
        passwordLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(passwordLabel, gbc);
        
        // Style password field
        passwordField.setFont(NeoBrutalLookAndFeel.FONT_GENERAL_SANS.deriveFont(14f));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NeoBrutalLookAndFeel.COLOR_ACCENT1, 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);
        
        // Password strength indicator (simple visual)
        JPanel strengthPanel = createPasswordStrengthIndicator();
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.insets = new Insets(5, 10, 15, 10);
        formPanel.add(strengthPanel, gbc);
        
        // Button panel
        JPanel buttonPanel = createStyledButtonPanel();
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 0, 10);
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createPasswordStrengthIndicator() {
        JPanel strengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        strengthPanel.setOpaque(false);
        
        JLabel strengthLabel = new JLabel("Strength:");
        strengthLabel.setFont(NeoBrutalLookAndFeel.FONT_PIXELIFY.deriveFont(12f));
        strengthLabel.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
        
        // Simple strength indicator bars
        JPanel barsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        barsPanel.setOpaque(false);
        
        for (int i = 0; i < 3; i++) {
            JPanel bar = new JPanel();
            bar.setPreferredSize(new Dimension(20, 4));
            bar.setBackground(NeoBrutalLookAndFeel.COLOR_BORDER);
            barsPanel.add(bar);
        }
        
        strengthPanel.add(strengthLabel);
        strengthPanel.add(barsPanel);
        
        // Add listener to update strength indicator
        passwordField.addCaretListener(e -> updatePasswordStrength(barsPanel));
        
        return strengthPanel;
    }
    
    private void updatePasswordStrength(JPanel barsPanel) {
        String password = new String(passwordField.getPassword());
        Component[] bars = barsPanel.getComponents();
        
        // Reset all bars
        for (Component bar : bars) {
            if (bar instanceof JPanel) {
                bar.setBackground(NeoBrutalLookAndFeel.COLOR_BORDER);
            }
        }
        
        // Color bars based on password strength
        if (password.length() >= 8) {
            if (bars.length > 0) bars[0].setBackground(NeoBrutalLookAndFeel.COLOR_ACCENT2);
        }
        if (password.length() >= 8 && password.matches(".*[A-Z].*")) {
            if (bars.length > 1) bars[1].setBackground(NeoBrutalLookAndFeel.COLOR_ACCENT1);
        }
        if (password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*")) {
            if (bars.length > 2) bars[2].setBackground(NeoBrutalLookAndFeel.COLOR_SECONDARY);
        }
    }
    
    private JPanel createStyledButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        // Style register button
        registerButton.setText("REGISTER");
        registerButton.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(18f));
        registerButton.setPreferredSize(new Dimension(140, 50));
        
        // Style back button
        backToLoginButton.setText("BACK");
        backToLoginButton.setFont(NeoBrutalLookAndFeel.FONT_BEBAS_NEUE.deriveFont(18f));
        backToLoginButton.setPreferredSize(new Dimension(120, 50));
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backToLoginButton);

        return buttonPanel;
    }
    
    
    private void setupActionListeners() {
        // Enhanced button hover effects
        registerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(NeoBrutalLookAndFeel.COLOR_ACCENT1);
                registerButton.setBorder(NeoBrutalLookAndFeel.createColoredShadow(
                    NeoBrutalLookAndFeel.COLOR_ACCENT1, 6, 8));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerButton.setBackground(NeoBrutalLookAndFeel.COLOR_SECONDARY);
                registerButton.setBorder(NeoBrutalLookAndFeel.BORDER_BUTTON_SHADOW);
            }
        });

        backToLoginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backToLoginButton.setBackground(NeoBrutalLookAndFeel.COLOR_PRIMARY);
                backToLoginButton.setForeground(NeoBrutalLookAndFeel.COLOR_WHITE);
                backToLoginButton.setBorder(NeoBrutalLookAndFeel.createColoredShadow(
                    NeoBrutalLookAndFeel.COLOR_PRIMARY, 6, 8));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backToLoginButton.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
                backToLoginButton.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
                backToLoginButton.setBorder(NeoBrutalLookAndFeel.BORDER_BUTTON_SHADOW);
            }
        });

        // Action listeners
        registerButton.addActionListener(e -> handleRegister());
        backToLoginButton.addActionListener(e -> mainFrame.showLoginPanel());
    }

    /**
     * Handles the register button click event.
     */
    private void handleRegister() {
        String fullName = fullNameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (fullName.isBlank() || email.isBlank() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);

        try {
            userDAO.registerUser(user);
            JOptionPane.showMessageDialog(this, "Registration Successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // On success, automatically navigate back to the login screen
            mainFrame.showLoginPanel();

        } catch (SQLException ex) {
            // Check for a duplicate email error specifically
            if (ex.getSQLState().equals("23505")) { // PostgreSQL unique violation code
                JOptionPane.showMessageDialog(this, "This email address is already registered.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "A database error occurred: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}