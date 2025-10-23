package com.note0.simple;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

/**
 * A utility class to hold all the common colors, fonts, and styles
 * for the application to ensure a consistent "dark" look and feel.
 * * This palette is inspired by the "Dracula" theme.
 */
public class UITheme {

    // --- Colors (Dracula Palette) ---
    // A very dark purple-gray for the main background
    public static final Color APP_BACKGROUND = new Color(40, 42, 54);
    // A lighter purple-gray for content cards
    public static final Color CARD_BACKGROUND = new Color(68, 71, 90);
    // An off-white for all text
    public static final Color TEXT_COLOR = new Color(248, 248, 242);
    // A light purple for borders
    public static final Color BORDER_COLOR = new Color(98, 114, 164);
    // A bright, "electric" cyan for primary actions
    public static final Color ACCENT_COLOR = new Color(80, 250, 212);
    // A bright pink for danger actions
    public static final Color DANGER_COLOR = new Color(255, 121, 198);
    // A calm blue-gray for secondary buttons
    public static final Color SECONDARY_COLOR = new Color(144, 153, 174);

    // --- Fonts ---
    public static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 28);
    public static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);
    
    // --- NEW FONT STYLES FOR TABLES ---
    public static final Font TABLE_HEADER_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font TABLE_BODY_FONT = new Font("SansSerif", Font.PLAIN, 16);

    // --- Borders ---
    public static final Border APP_PADDING = BorderFactory.createEmptyBorder(20, 20, 20, 20);
    
    // --- NEW METRICS FOR TABLES ---
    public static final int TABLE_ROW_HEIGHT = 26; // (16pt font + 10px padding)
    
    // A border for secondary buttons (dark card, light text)
    private static final Border SECONDARY_BUTTON_BORDER = new CompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 2), // Use secondary color
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
    );

    /**
     * Creates the neo-brutalism shadow border, adapted for a dark theme.
     * It has a light purple border and a black shadow.
     * @return A composed Border object.
     */
    public static Border createShadowBorder() {
        Border contentBorder = new CompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2), // The light purple border
                BorderFactory.createEmptyBorder(15, 15, 15, 15)  // 15px internal padding
        );
        
        // The shadow is a simple black offset border
        Border shadow = BorderFactory.createMatteBorder(0, 0, 4, 4, Color.BLACK);

        // Combine the shadow (outside) and the content border (inside)
        return new CompoundBorder(shadow, contentBorder);
    }

    /**
     * Styles a JButton as a primary action button (Bright Cyan).
     * @param button The JButton to style.
     */
    public static void stylePrimaryButton(JButton button) {
        Border border = new CompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2), // Black outline
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        );
        button.setBackground(ACCENT_COLOR); // Bright Cyan
        button.setForeground(Color.BLACK);  // Black text for high contrast
        button.setFont(LABEL_FONT);
        button.setBorder(border);
        button.setOpaque(true);
    }

    /**
     * Styles a JButton as a "secondary" button (Gray on Dark).
     * @param button The JButton to style.
     */
    public static void styleSecondaryButton(JButton button) {
        button.setBackground(CARD_BACKGROUND); // Dark card color
        button.setForeground(TEXT_COLOR);      // Light text
        button.setFont(LABEL_FONT);
        button.setBorder(SECONDARY_BUTTON_BORDER); // Light purple border
        button.setOpaque(true);
    }

    /**
     * Styles a JButton as a "danger" button (Bright Pink).
     * @param button The JButton to style.
     */
    public static void styleDangerButton(JButton button) {
        Border border = new CompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2), // Black outline
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        );
        button.setBackground(DANGER_COLOR); // Bright Pink
        button.setForeground(Color.BLACK);  // Black text for high contrast
        button.setFont(LABEL_FONT);
        button.setBorder(border);
        button.setOpaque(true);
    }
}

