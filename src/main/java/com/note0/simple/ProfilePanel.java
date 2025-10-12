package com.note0.simple;

import javax.swing.*;
import java.awt.*;

public class ProfilePanel extends JPanel {

    private final UserDAO userDAO;
    private final User currentUser;

    private JTextField fullNameField = new JTextField(20);
    private JTextField emailField = new JTextField(20);
    private JTextField collegeNameField = new JTextField(20);
    private JSpinner semesterSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
    private JButton updateButton = new JButton("Update Profile");

    public ProfilePanel(UserDAO userDAO, User currentUser) {
        this.userDAO = userDAO;
        this.currentUser = currentUser;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Your Profile"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; add(fullNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("College Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(collegeNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Semester:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; add(semesterSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(updateButton, gbc);

        updateButton.addActionListener(e -> updateUserProfile());

        loadProfile();
    }

    private void loadProfile() {
        fullNameField.setText(currentUser.getFullName());
        emailField.setText(currentUser.getEmail());
        collegeNameField.setText(currentUser.getCollegeName());
        semesterSpinner.setValue(currentUser.getSemester());
    }

    private void updateUserProfile() {
        currentUser.setFullName(fullNameField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setCollegeName(collegeNameField.getText());
        currentUser.setSemester((int) semesterSpinner.getValue());

        try {
            userDAO.updateUser(currentUser);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
