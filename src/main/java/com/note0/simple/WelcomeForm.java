package com.note0.simple;

import javax.swing.*;
import java.awt.*;

public class WelcomeForm extends JFrame {

    public WelcomeForm(User user) {
        setTitle("Note0 - Welcome!");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a label to welcome the user by name
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getFullName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 24));

        add(welcomeLabel);
    }
}