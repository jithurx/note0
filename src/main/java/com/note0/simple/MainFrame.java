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

    private JPanel mainPanel;
    private CardLayout cardLayout;

    private final UserDAO userDAO;
    private final MaterialDAO materialDAO;
    private final SubjectDAO subjectDAO;
    private final CloudinaryService cloudinaryService;

    public MainFrame() {
        this.userDAO = new UserDAO();
        this.materialDAO = new MaterialDAO();
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
        cardLayout.show(mainPanel, "LOGIN_PANEL");
    }

    public void showRegistrationPanel() {
        cardLayout.show(mainPanel, "REGISTER_PANEL");
    }

    public void showFeedPanel(User user) {
        JTabbedPane tabbedPane = new JTabbedPane();

        FeedPanel feedPanel = new FeedPanel(this, user, materialDAO, subjectDAO, cloudinaryService);
        tabbedPane.addTab("Home", feedPanel);

        DashboardPanel dashboardPanel = new DashboardPanel(this, user, materialDAO, subjectDAO, cloudinaryService);
        tabbedPane.addTab("Browse", dashboardPanel);
        
        ProfilePanel profilePanel = new ProfilePanel(userDAO, user);
        tabbedPane.addTab("Profile", profilePanel);

        if ("ADMIN".equals(user.getRole())) {
            AdminPanel adminPanel = new AdminPanel(subjectDAO, materialDAO);
            tabbedPane.addTab("Admin", adminPanel);
        }

        mainPanel.add(tabbedPane, "MAIN_APP_PANEL");
        cardLayout.show(mainPanel, "MAIN_APP_PANEL");
    }

    public void showDashboardPanel(User user) {
        // This method is now effectively replaced by the tabbed pane in showFeedPanel
        // but we can keep it for potential future use or specific navigation needs.
        showFeedPanel(user); 
    }
}