package com.note0.simple;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ProfilePanel extends JPanel {

    private final UserDAO userDAO;
    private final User loggedInUser;

    public ProfilePanel(UserDAO userDAO, User user) {
        this.userDAO = userDAO;
        this.loggedInUser = user;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        add(new JLabel("Full Name:"), gbc);
        gbc.gridy++;
        add(new JLabel("Username:"), gbc);
        gbc.gridy++;
        add(new JLabel("Role:"), gbc);
        gbc.gridy++;
        add(new JLabel("Semester:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField fullNameField = new JTextField(loggedInUser.getFullName(), 20);
        JTextField usernameField = new JTextField(loggedInUser.getUsername(), 20);
        usernameField.setEditable(false);
        JTextField roleField = new JTextField(loggedInUser.getRole(), 20);
        roleField.setEditable(false);
        JTextField semesterField = new JTextField(String.valueOf(loggedInUser.getSemester()), 20);

        add(fullNameField, gbc);
        gbc.gridy++;
        add(usernameField, gbc);
        gbc.gridy++;
        add(roleField, gbc);
        gbc.gridy++;
        add(semesterField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton updateButton = new JButton("Update Profile");
        add(updateButton, gbc);

        updateButton.addActionListener(e -> {
            String newFullName = fullNameField.getText();
            int newSemester = Integer.parseInt(semesterField.getText());

            try {
                userDAO.updateUser(loggedInUser.getId(), newFullName, newSemester);
                loggedInUser.setFullName(newFullName);
                loggedInUser.setSemester(newSemester);
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
