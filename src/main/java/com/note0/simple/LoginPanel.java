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

    public LoginPanel(MainFrame mainFrame, UserDAO userDAO) {
        this.mainFrame = mainFrame;
        this.userDAO = userDAO;

        // Use a more flexible layout manager for better component arrangement
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between components

        // --- UI Layout ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);
        
        // Panel to hold the buttons side-by-side
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(registerNavButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Make this component span two columns
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // --- Action Listeners ---
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
                // Tell the MainFrame to create and show the dashboard for this user.
                mainFrame.showDashboardPanel(loggedInUser);
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