package com.note0.simple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * A reusable neobrutalist navigation bar component.
 * Provides styled navigation buttons with active state indication.
 */
public class NavigationBar extends JPanel {
    
    private Map<String, JButton> buttons;
    private String activeButton;
    private ActionListener buttonListener;
    
    // Button configurations
    public enum ButtonType {
        HOME("🏠", "Home", NeoBrutalLookAndFeel.COLOR_PRIMARY),
        BROWSE("📚", "Browse All", NeoBrutalLookAndFeel.COLOR_SECONDARY),
        UPLOAD("⬆️", "Upload", NeoBrutalLookAndFeel.COLOR_ACCENT1),
        PROFILE("👤", "Profile", NeoBrutalLookAndFeel.COLOR_ACCENT2),
        LOGOUT("🚪", "Logout", NeoBrutalLookAndFeel.COLOR_BORDER);
        
        private final String icon;
        private final String text;
        private final Color color;
        
        ButtonType(String icon, String text, Color color) {
            this.icon = icon;
            this.text = text;
            this.color = color;
        }
        
        public String getIcon() { return icon; }
        public String getText() { return text; }
        public Color getColor() { return color; }
    }
    
    public NavigationBar(ActionListener buttonListener) {
        this.buttonListener = buttonListener;
        this.buttons = new HashMap<>();
        this.activeButton = null;
        
        setupNavigationBar();
    }
    
    private void setupNavigationBar() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        setBackground(NeoBrutalLookAndFeel.COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Create navigation buttons
        for (ButtonType buttonType : ButtonType.values()) {
            JButton button = createNavigationButton(buttonType);
            buttons.put(buttonType.name(), button);
            add(button);
        }
    }
    
    private JButton createNavigationButton(ButtonType buttonType) {
        JButton button = new JButton();
        
        // Create button text with icon
        String buttonText = buttonType.getIcon() + " " + buttonType.getText();
        button.setText(buttonText);
        
        // Style the button
        button.setFont(NeoBrutalLookAndFeel.FONT_PIXELIFY.deriveFont(10f));
        button.setPreferredSize(new Dimension(80, 40));
        button.setFocusPainted(false);
        
        // Add action listener
        button.addActionListener(e -> {
            if (buttonListener != null) {
                buttonListener.actionPerformed(e);
            }
        });
        
        // Set action command for identification
        button.setActionCommand(buttonType.name());
        
        // Add hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!buttonType.name().equals(activeButton)) {
                    button.setBackground(buttonType.getColor());
                    button.setForeground(NeoBrutalLookAndFeel.COLOR_WHITE);
                    button.setBorder(NeoBrutalLookAndFeel.createColoredShadow(buttonType.getColor(), 6, 8));
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!buttonType.name().equals(activeButton)) {
                    button.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
                    button.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
                    button.setBorder(NeoBrutalLookAndFeel.BORDER_BUTTON_SHADOW);
                }
            }
        });
        
        return button;
    }
    
    /**
     * Sets the active button and updates styling
     */
    public void setActiveButton(String buttonName) {
        // Reset previous active button
        if (activeButton != null && buttons.containsKey(activeButton)) {
            JButton prevButton = buttons.get(activeButton);
            prevButton.setBackground(NeoBrutalLookAndFeel.COLOR_WHITE);
            prevButton.setForeground(NeoBrutalLookAndFeel.COLOR_TEXT);
            prevButton.setBorder(NeoBrutalLookAndFeel.BORDER_BUTTON_SHADOW);
        }
        
        // Set new active button
        if (buttons.containsKey(buttonName)) {
            activeButton = buttonName;
            JButton activeBtn = buttons.get(buttonName);
            
            // Find the button type for color
            ButtonType buttonType = ButtonType.valueOf(buttonName);
            activeBtn.setBackground(buttonType.getColor());
            activeBtn.setForeground(NeoBrutalLookAndFeel.COLOR_WHITE);
            activeBtn.setBorder(NeoBrutalLookAndFeel.createColoredShadow(buttonType.getColor(), 8, 8));
        }
    }
    
    /**
     * Creates a navigation bar with only specific buttons
     */
    public static NavigationBar createWithButtons(ButtonType[] buttonTypes, ActionListener listener) {
        NavigationBar navBar = new NavigationBar(listener);
        
        // Clear existing buttons
        navBar.removeAll();
        navBar.buttons.clear();
        
        // Add only specified buttons
        for (ButtonType buttonType : buttonTypes) {
            JButton button = navBar.createNavigationButton(buttonType);
            navBar.buttons.put(buttonType.name(), button);
            navBar.add(button);
        }
        
        navBar.revalidate();
        navBar.repaint();
        
        return navBar;
    }
    
    /**
     * Creates a minimal navigation bar for login/register screens
     */
    public static NavigationBar createMinimal(ActionListener listener) {
        return createWithButtons(new ButtonType[]{}, listener);
    }
    
    /**
     * Creates a full navigation bar for authenticated screens
     */
    public static NavigationBar createFull(ActionListener listener) {
        return createWithButtons(ButtonType.values(), listener);
    }
    
    /**
     * Creates a feed navigation bar (home, browse, upload, logout)
     */
    public static NavigationBar createFeed(ActionListener listener) {
        return createWithButtons(new ButtonType[]{
            ButtonType.HOME,
            ButtonType.BROWSE,
            ButtonType.UPLOAD,
            ButtonType.LOGOUT
        }, listener);
    }
    
    /**
     * Creates a dashboard navigation bar (browse, upload, back to feed, logout)
     */
    public static NavigationBar createDashboard(ActionListener listener) {
        return createWithButtons(new ButtonType[]{
            ButtonType.HOME,
            ButtonType.BROWSE,
            ButtonType.UPLOAD,
            ButtonType.LOGOUT
        }, listener);
    }
    
    // Getters
    public String getActiveButton() { return activeButton; }
    public Map<String, JButton> getButtons() { return buttons; }
    
    // Utility method to enable/disable specific buttons
    public void setButtonEnabled(String buttonName, boolean enabled) {
        if (buttons.containsKey(buttonName)) {
            buttons.get(buttonName).setEnabled(enabled);
        }
    }
}


