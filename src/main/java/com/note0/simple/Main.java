package com.note0.simple;

import javax.swing.SwingUtilities;

/**
 * The main entry point for the Note0 Desktop Application.
 * 
 * This class is responsible for initializing and launching the main application window.
 * It uses SwingUtilities.invokeLater to ensure that the GUI is created and updated
 * on the Event Dispatch Thread (EDT), which is the standard and required practice
 * for thread-safe Swing applications.
 */
public class Main {

    public static void main(String[] args) {
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create the main application frame and make it visible.
                // The MainFrame will handle all other components and logic from here.
                new MainFrame().setVisible(true);
            }
        });
    }
}