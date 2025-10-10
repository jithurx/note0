package com.java.note0.simple;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // This is the safe and correct way to start a Swing application.
        // It ensures that all UI updates happen on the correct thread.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create our registration form and make it visible.
                new RegistrationForm().setVisible(true);
            }
        });
    }
}