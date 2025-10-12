package com.note0.simple;

import javax.swing.*;
import java.awt.*;

/**
 * The main window of the application.
 * This class holds and manages the different panels (Login, Register, Feed, Dashboard)
 * using a CardLayout, allowing for seamless navigation between different views
 * without opening or closing new windows.
 * 
 * New Navigation Flow: Login -> Main App (with tabs) -> specific tabs
 */
public class MainFrame extends JFrame {

    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private JTabbedPane tabbedPane; // Main application tabs

    private final UserDAO userDAO;
    private final MaterialDAO materialDAO;
    private final SubjectDAO subjectDAO;
    private final CloudinaryService cloudinaryService;

    public MainFrame() {
        this.userDAO = new UserDAO();
        this.materialDAO = new MaterialDAO(); // FIX: Removed UserDAO dependency
        this.subjectDAO = new SubjectDAO();
        this.cloudinaryService = new CloudinaryService();

        setTitle("Note0 - Note Sharing Application");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        LoginPanel loginPanel = new LoginPanel(this, userDAO);
        RegistrationPanel registrationPanel = new RegistrationPanel(this, userDAO);

        mainPanel.add(loginPanel, "LOGIN_PANEL");
        mainPanel.add(registrationPanel, "REGISTER_PANEL");

        add(mainPanel);

        cardLayout.show(mainPanel, "LOGIN_PANEL");
    }

    public void showLoginPanel() {
        // Reset the tabbed pane on logout
        if (tabbedPane != null) {
            mainPanel.remove(tabbedPane);
            tabbedPane = null;
        }
        cardLayout.show(mainPanel, "LOGIN_PANEL");
    }

    public void showRegistrationPanel() {
        cardLayout.show(mainPanel, "REGISTER_PANEL");
    }

    /**
     * Shows the main application panel with tabs. If not already created,
     * it initializes the tabbed pane and all the main panels.
     * @param user The logged-in user.
     */
    public void showFeedPanel(User user) {
        if (tabbedPane == null) {
            tabbedPane = new JTabbedPane();

            FeedPanel feedPanel = new FeedPanel(this, user, materialDAO, subjectDAO, cloudinaryService);
            tabbedPane.addTab("Home", feedPanel);

            DashboardPanel dashboardPanel = new DashboardPanel(this, user, materialDAO, subjectDAO, cloudinaryService);
            tabbedPane.addTab("Browse", dashboardPanel);

            ProfilePanel profilePanel = new ProfilePanel(userDAO, user);
            tabbedPane.addTab("Profile", profilePanel);

            if ("ADMIN".equals(user.getRole())) {
                AdminForm adminForm = new AdminForm(subjectDAO, userDAO, materialDAO);
                tabbedPane.addTab("Admin", adminForm);
            }

            mainPanel.add(tabbedPane, "MAIN_APP_PANEL");
        }
        cardLayout.show(mainPanel, "MAIN_APP_PANEL");
    }

    /**
     * Switches the view to the "Browse" tab in the main application panel.
     * @param user The logged-in user (required to ensure the panel is shown).
     */
    public void showDashboardPanel(User user) {
        // Ensure the main app panel is visible first
        showFeedPanel(user);
        // Switch to the 'Browse' tab (index 1)
        if (tabbedPane != null) {
            tabbedPane.setSelectedIndex(1);
        }
    }
}
