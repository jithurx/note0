package com.note0.simple;

import javax.swing.*;
import java.awt.*;

import java.sql.SQLException; // This import is required for the try-catch block

public class LoginForm extends JFrame {

    // The login form needs a reference to the UserDAO to check credentials
    private final UserDAO userDAO;

    // UI Components
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton loginButton = new JButton("Login");
    private final JButton registerNavButton = new JButton("Create New Account");

    public LoginForm() {
        this.userDAO = new UserDAO(); // Create our database worker

        setTitle("Note0 - Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        panel.add(loginButton);
        panel.add(registerNavButton);

        add(panel);

        // --- Action Listeners ---
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Simple validation to ensure fields are not empty
            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter email and password.");
                return;
            }

            try {
                // Call the DAO's login method to check credentials
                User loggedInUser = userDAO.loginUser(email, password);

                if (loggedInUser != null) {
                    // If the returned user is not null, login was successful
                    JOptionPane.showMessageDialog(null, "Login Successful!");

                    // Close the current login window
                    dispose();

                    // Create and show the main DashboardForm with all required dependencies
                    DashboardForm dashboard = new DashboardForm(loggedInUser, new SubjectDAO(), new MaterialDAO());
                    dashboard.setVisible(true);

                } else {
                    // If the returned user is null, it means email/password was incorrect
                    JOptionPane.showMessageDialog(null, "Invalid email or password.");
                }

            } catch (SQLException ex) {
                // Catch any potential database connection or query errors
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            }
        });

        // This listener handles navigation to the registration form.
        registerNavButton.addActionListener(e -> {
            // Create a new instance of the RegistrationForm and make it visible
            new RegistrationForm().setVisible(true);
        });
    }
}