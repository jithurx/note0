package com.note0.simple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationForm extends JFrame {

    // The UI needs a reference to the DAO to do its work
    private UserDAO userDAO;

    // Swing components for our form
    private JTextField fullNameField = new JTextField();
    private JTextField emailField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JButton registerButton = new JButton("Register");

    public RegistrationForm() {
        this.userDAO = new UserDAO(); // Create our database worker

        // Basic window setup
        setTitle("Note0 - Register");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Use a panel with a grid layout to organize our components
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add labels and fields to the panel
        panel.add(new JLabel("Full Name:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel()); // Empty spacer
        panel.add(registerButton);

        add(panel); // Add the panel to the window

        // This is the most important part: what happens when the button is clicked?
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the text from the input fields
                String fullName = fullNameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                // Create a User object to hold this data
                User user = new User();
                user.setFullName(fullName);
                user.setEmail(email);
                user.setPassword(password);

                try {
                    // Call the DAO to save the user to the database
                    userDAO.registerUser(user);
                    // Show a success popup message
                    JOptionPane.showMessageDialog(null, "Registration Successful!");
                } catch (Exception ex) {
                    // Show an error popup message
                    JOptionPane.showMessageDialog(null, "Registration Failed: " + ex.getMessage());
                }
            }
        });
    }
}