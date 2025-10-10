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

    public RegistrationPanel(MainFrame mainFrame, UserDAO userDAO) {
        this.mainFrame = mainFrame;
        this.userDAO = userDAO;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding

        // --- UI Layout ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Full Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        add(passwordField, gbc);
        
        // Panel to hold the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(backToLoginButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // --- Action Listeners ---
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