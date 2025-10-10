package com.note0.simple;

import javax.swing.*;
import java.awt.*;

/**
 * The main window of the application.
 * This class holds and manages the different panels (Login, Register, Dashboard)
 * using a CardLayout, allowing for seamless navigation between different views
 * without opening or closing new windows.
 */
public class MainFrame extends JFrame {

    // The panel that uses CardLayout to switch between other panels
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // DAOs are created once here and passed to the panels that need them.
    // This is more efficient than each panel creating its own DAO.
    private final UserDAO userDAO;
    private final MaterialDAO materialDAO;
    private final SubjectDAO subjectDAO;
    private final CloudinaryService cloudinaryService;

    public MainFrame() {
        // Initialize all the Data Access Objects
        this.userDAO = new UserDAO();
        this.materialDAO = new MaterialDAO();
        this.subjectDAO = new SubjectDAO();
        this.cloudinaryService = new CloudinaryService();

        // Basic JFrame setup
        setTitle("Note0 - Note Sharing Application");
        setSize(1024, 768); // A more modern, larger default size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen

        // Initialize the CardLayout and the main panel that will hold our "cards"
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create instances of our different UI panels
        // We pass 'this' (a reference to this MainFrame) so the panels can tell it to switch views.
        LoginPanel loginPanel = new LoginPanel(this, userDAO);
        RegistrationPanel registrationPanel = new RegistrationPanel(this, userDAO);
        // The DashboardPanel will be created upon successful login.

        // Add the panels to the main panel with unique names (keys) for the CardLayout
        mainPanel.add(loginPanel, "LOGIN_PANEL");
        mainPanel.add(registrationPanel, "REGISTER_PANEL");

        // Add the main panel to the JFrame
        add(mainPanel);

        // Show the initial panel
        cardLayout.show(mainPanel, "LOGIN_PANEL");
    }

    // --- Public Navigation Methods ---
    // These methods will be called by the panels to switch the view.

    public void showLoginPanel() {
        cardLayout.show(mainPanel, "LOGIN_PANEL");
    }

    public void showRegistrationPanel() {
        cardLayout.show(mainPanel, "REGISTER_PANEL");
    }

    /**
     * Called after a user successfully logs in.
     * It creates a new DashboardPanel, adds it to the CardLayout, and switches to it.
     * @param user The user who has just logged in.
     */
    public void showDashboardPanel(User user) {
        // Create the dashboard panel, passing all necessary DAOs and the logged-in user
        DashboardPanel dashboardPanel = new DashboardPanel(this, user, materialDAO, subjectDAO, cloudinaryService);
        
        // Add the dashboard panel to our card deck
        mainPanel.add(dashboardPanel, "DASHBOARD_PANEL");

        // Switch the view to the newly created dashboard
        cardLayout.show(mainPanel, "DASHBOARD_PANEL");
    }
}